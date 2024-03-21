/**
 * 
 */

// SockJS와 Stomp를 사용하여 서버의 WebSocket에 연결
let socket = new SockJS('/ws-stomp');
stompClient = Stomp.over(socket);

let localStreamElement = document.querySelector('#localStream');

const myKey = Math.random().toString(36).substring(2,11);
let pcListMap = new Map();
let otherKeyList = [];
let localStream;

//역할 저장
let role;

if (anonymous.role == 'answerA') {
    role = room.answerA;
}
else if (anonymous.role == 'answerB'){
    role = room.answerB;
}
else {
    role = '심판';
}



const startCam = async () =>{
    if(navigator.mediaDevices !== undefined){
        await navigator.mediaDevices.getUserMedia({ audio: true, video : true })
            .then(async (stream) => {
                console.log('Stream found');
                                //웹캠, 마이크의 스트림 정보를 글로벌 변수로 저장한다.
                localStream = stream;
                // Disable the microphone by default
                stream.getAudioTracks()[0].enabled = true;
                localStreamElement.srcObject = localStream;
                // Connect after making sure that local stream is availble
   
            }).catch(error => {
                console.error("Error accessing media devices:", error);
            });
    }
   
}




let timerInterval;

function connect() {
    startCam();
    Swal.fire({
        title: "주제 : "+room.title,
        html: "회원님의 역할은 <b>"+role+"</b> 입니다",
        timer: 5000,
        timerProgressBar: true,
        allowOutsideClick: false,
        didOpen: () => {
            Swal.showLoading();
            // const timer = Swal.getPopup().querySelector("b");
            let timeLeft = 4; // 남은 시간 설정 (10초)
            timerInterval = setInterval(() => {
                timer.textContent = `${timeLeft}`;
                timeLeft--; // 시간 감소
            }, 1000); // 1초마다 실행
        },
        willClose: () => {
            clearInterval(timerInterval);
        }
        }).then((result) => {
            if (result.dismiss === Swal.DismissReason.timer) {
                console.log("실행");
                // alert('시작');
                stompClient.connect({}, function() {
                    console.log('연결- ');
        
                                     //iceCandidate peer 교환을 위한 subscribe
                    stompClient.subscribe(`/room/peer/iceCandidate/${myKey}/${room.roomId}`, candidate => {
                        const key = JSON.parse(candidate.body).key
                        const message = JSON.parse(candidate.body).body;
            
                                    // 해당 key에 해당되는 peer 에 받은 정보를 addIceCandidate 해준다.
                        pcListMap.get(key).addIceCandidate(new RTCIceCandidate({candidate:message.candidate,sdpMLineIndex:message.sdpMLineIndex,sdpMid:message.sdpMid}));
            
                    });
        
                                //offer peer 교환을 위한 subscribe
                    stompClient.subscribe(`/room/peer/offer/${myKey}/${room.roomId}`, offer => {
                        const key = JSON.parse(offer.body).key;
                        const message = JSON.parse(offer.body).body;
                                    
                                    // 해당 key에 새로운 peerConnection 를 생성해준후 pcListMap 에 저장해준다.
                        pcListMap.set(key,createPeerConnection(key));
                                    // 생성한 peer 에 offer정보를 setRemoteDescription 해준다.
                        pcListMap.get(key).setRemoteDescription(new RTCSessionDescription({type:message.type,sdp:message.sdp}));
                        //sendAnswer 함수를 호출해준다.
                                    sendAnswer(pcListMap.get(key), key);
            
                    });
        
                    //answer peer 교환을 위한 subscribe
                    stompClient.subscribe(`/room/peer/answer/${myKey}/${room.roomId}`, answer =>{
                        const key = JSON.parse(answer.body).key;
                        const message = JSON.parse(answer.body).body;
                                    
                                    // 해당 key에 해당되는 Peer 에 받은 정보를 setRemoteDescription 해준다.
                        pcListMap.get(key).setRemoteDescription(new RTCSessionDescription(message));
            
                    });
        
                    //key를 보내라는 신호를 받은 subscribe
                    stompClient.subscribe(`/room/call/key`, message =>{
                                //자신의 key를 보내는 send
                    stompClient.send(`/send/send/key`, {}, JSON.stringify(myKey));
        
                    });
        
                    //상대방의 key를 받는 subscribe
                    stompClient.subscribe(`/room/send/key`, message => {
                        const key = JSON.parse(message.body);
                                    
                                    //만약 중복되는 키가 ohterKeyList에 있는지 확인하고 없다면 추가해준다.
                        if(myKey !== key && otherKeyList.find((mapKey) => mapKey === myKey) === undefined){
                            otherKeyList.push(key);
                        }
                    });
        
                    stompClient.send(`/send/call/key`, {}, {});
            
                    setTimeout(() =>{
                    
                        otherKeyList.map((key) =>{
                            if(!pcListMap.has(key)){
                                pcListMap.set(key, createPeerConnection(key));
                                sendOffer(pcListMap.get(key),key);
                            }
                    
                        });
                    
                    },1000);
                });
            }
        });


}


    
if(localStream !== undefined){
    document.querySelector('#localStream').style.display = 'block';
   //  document.querySelector('#startSteamBtn').style.display = '';
}


    
     setTimeout(() =>{
    
         otherKeyList.map((key) =>{
             if(!pcListMap.has(key)){
                 pcListMap.set(key, createPeerConnection(key));
                 sendOffer(pcListMap.get(key),key);
             }
    
         });
    
     },1000);

const createPeerConnection = (otherKey) =>{
    const pc = new RTCPeerConnection();
    try {
                // peerConnection 에서 icecandidate 이벤트가 발생시 onIceCandidate 함수 실행
        pc.addEventListener('icecandidate', (event) =>{
            onIceCandidate(event, otherKey);
        });
                // peerConnection 에서 track 이벤트가 발생시 onTrack 함수를 실행
        pc.addEventListener('track', (event) =>{
            onTrack(event, otherKey);
        });
   
                // 만약 localStream 이 존재하면 peerConnection에 addTrack 으로 추가함
        if(localStream !== undefined){
            localStream.getTracks().forEach(track => {
                pc.addTrack(track, localStream);
            });
        }
        console.log('PeerConnection created');
    } catch (error) {
        console.error('PeerConnection failed: ', error);
    }
    return pc;
}

//onIceCandidate
let onIceCandidate = (event, otherKey) => {
    if (event.candidate) {
        console.log('ICE candidate');
        stompClient.send(`/send/peer/iceCandidate/${otherKey}/${room.roomId}`,{}, JSON.stringify({
            key : myKey,
            body : event.candidate
        }));
    }
};
   
//onTrack
let onTrack = (event, otherKey) => {
    if(document.getElementById(`${otherKey}`) === null){
        const video =  document.createElement('video');
   
        video.autoplay = true;
        video.controls = true;
        video.id = otherKey;
        video.srcObject = event.streams[0];
   
        document.getElementById('remoteStreamDiv').appendChild(video);
    }
};

let sendOffer = (pc ,otherKey) => {
    pc.createOffer().then(offer =>{
        setLocalAndSendMessage(pc, offer);
        stompClient.send(`/send/peer/offer/${otherKey}/${room.roomId}`, {}, JSON.stringify({
            key : myKey,
            body : offer
        }));
        console.log('Send offer');
    });
};
   
let sendAnswer = (pc,otherKey) => {
    pc.createAnswer().then( answer => {
        setLocalAndSendMessage(pc ,answer);
        stompClient.send(`/send/peer/answer/${otherKey}/${room.roomId}`, {}, JSON.stringify({
            key : myKey,
            body : answer
        }));
        console.log('Send answer');
    });
};
   
const setLocalAndSendMessage = (pc ,sessionDescription) =>{
    pc.setLocalDescription(sessionDescription);
}

window.onload = function (){
    connect();
}




