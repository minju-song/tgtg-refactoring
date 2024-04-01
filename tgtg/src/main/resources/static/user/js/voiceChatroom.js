/**
 * 
 */



let localStreamElement = document.querySelector('#localStream');

const myKey = Math.random().toString(36).substring(2,11);
let pcListMap = new Map();
let otherKeyList = [];
let localStream;

//음소거
let isVoiceMute = false;

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

// 음소거
function voiceMute() {
    updateVoiceMuteButton();

    const audioTrack = localStream.getAudioTracks()[0];
    audioTrack.enabled = !audioTrack.enabled; // 현재 상태를 반전시켜 음소거/음소거 해제

}

// 음소거 버튼 바꾸기
function updateVoiceMuteButton() {
    isVoiceMute = !isVoiceMute;
    if(isVoiceMute) {
        document.getElementById('voiceMuteBtn').innerText = '음소거 해제';
    }
    else {
        document.getElementById('voiceMuteBtn').innerText = '음소거';      
    }
}

// 스트림 
const startCam = async () =>{
    if(navigator.mediaDevices !== undefined){
        await navigator.mediaDevices.getUserMedia({ audio: true, video : false })
            .then(async (stream) => {
                console.log('Stream found');
                //웹캠, 마이크의 스트림 정보를 글로벌 변수로 저장한다.
                localStream = stream;
                // Disable the microphone by default
                stream.getAudioTracks()[0].enabled = true;
                // localStreamElement.srcObject = localStream;
                // Connect after making sure that local stream is availble
   
            }).catch(error => {
                console.error("Error accessing media devices:", error);
            });
    }
   
}

let timerInterval;

function connect() {
    return new Promise((resolve, reject) => {
        console.log(myKey);
        // SockJS와 Stomp를 사용하여 서버의 WebSocket에 연결
        let socket = new SockJS('/ws-stomp');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function () {
            console.log('연결- ');

            stompClient.subscribe('/room/'+room.roomId+'/connect', function (connectedCount) {
                showConnectedCount(JSON.parse(connectedCount.body));
            });

            //해당 채팅방 구독
            stompClient.subscribe('/room/'+room.roomId + '/game', function (chatMessage) {
                
                // 채팅 수신되면 화면에 그려줌
                showChat(JSON.parse(chatMessage.body));
            }); 
    
            //iceCandidate peer 교환을 위한 subscribe
            stompClient.subscribe('/room/peer/iceCandidate/'+myKey+'/'+room.roomId, candidate => {
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
            stompClient.subscribe(`/room/call/key/${room.roomId}`, message =>{
                        //자신의 key를 보내는 send
                stompClient.send(`/send/send/key/${room.roomId}`, {}, JSON.stringify(myKey));
    
            });
            
    
            //상대방의 key를 받는 subscribe
            stompClient.subscribe(`/room/send/key/${room.roomId}`, message => {
                const key = JSON.parse(message.body);
                console.log(key+"키 받음 ~")
                            
                            //만약 중복되는 키가 ohterKeyList에 있는지 확인하고 없다면 추가해준다.
                if(myKey !== key && otherKeyList.find((mapKey) => mapKey === myKey) === undefined){
                    otherKeyList.push(key);
                }
            });

            anonymous.key = myKey;
            // 채팅방에 접속했음을 서버에 알림
            stompClient.send("/send/"+room.roomId+"/enter", {}, JSON.stringify(anonymous));
            
            //타이머 현재시간
            stompClient.subscribe('/room/' + room.roomId + '/sendTime', function (endTime) {
                gameTimer(JSON.parse(endTime.body));
            });

            //심판 투표 결과
            stompClient.subscribe('/room/'+room.roomId+'/getResult', function (vote) {
                showResult(vote.body);
            });
    
            resolve(); // stompClient.connect 성공 시 resolve 호출
        }, function(error) {
          console.log('연결 실패: ', error);
          reject(error); // stompClient.connect 실패 시 reject 호출
        });

        Swal.fire({
            title: room.answerA+"<br> VS <br> "+room.answerB,
            html: "회원님의 역할은 <b>"+role+"</b> 입니다",
            timer: 5000,
            timerProgressBar: true,
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading();
                // const timer = Swal.getPopup().querySelector("b");
                let timeLeft = 4; // 남은 시간 설정 (10초)
                timerInterval = setInterval(() => {
                    // timer.textContent = `${timeLeft}`;
                    timeLeft--; // 시간 감소
                }, 1000); // 1초마다 실행
            },
            willClose: () => {
                clearInterval(timerInterval);
            }
            }).then((result) => {
                if (result.dismiss === Swal.DismissReason.timer) {
                    console.log('닫힘');
                    stompClient.send("/send/" + room.roomId + "/sendTime", {});
                }
            });
    });
    
}
   
// 스트림 버튼 클릭시 , 다른 웹 key들 웹소켓을 가져 온뒤에 offer -> answer -> iceCandidate 통신
// peer 커넥션은 pcListMap 으로 저장
async function sendKey() {

    await stompClient.send(`/send/call/key/${room.roomId}`, {}, {});

    setTimeout(() => {
        console.log('실행');
        otherKeyList.map((key) => {
            console.log('key : ' + key);
            if (!pcListMap.has(key)) {
                pcListMap.set(key, createPeerConnection(key));
                if(anonymous.role != 'judge'){
                    sendOffer(pcListMap.get(key), key);
                }

            }
        });
    }, 3000);
}


const createPeerConnection = (otherKey) =>{
    console.log('뿡뿡');
    const pc = new RTCPeerConnection();
    try {
                // peerConnection 에서 icecandidate 이벤트가 발생시 onIceCandidate 함수 실행
        pc.addEventListener('icecandidate', (event) =>{
            onIceCandidate(event, otherKey);
        });
                // peerConnection 에서 track 이벤트가 발생시 onTrack 함수를 실행
        pc.addEventListener('track', (event) =>{
            console.log('ontrack실행')
            onTrack(event, otherKey);
            if (event.track.kind === 'audio') {
                // 오디오 트랙을 발견하면, 이를 사용하여 MediaStream을 생성
                const receivedAudioStream = new MediaStream([event.track]);
        
                // 이제 receivedAudioStream을 사용하여 볼륨 측정 등의 작업을 수행할 수 있습니다.
                monitorVolume(receivedAudioStream, 100, otherKey); // 앞서 설명한 볼륨 모니터링 함수 호출
            }
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
    console.log("!!!!!!!!!!!!!!!!!!!!"+event.streams);
    if(document.getElementById(`${otherKey}`) === null){
        const video =  document.createElement('audio');
   
        video.autoplay = true;
        video.controls = true;
        video.id = otherKey;
        video.hidden = true;
        // video.muted = true;
        video.srcObject = event.streams[0];
        video.play().catch(e => console.error('비디오 재생 실패:', e));
   
        document.getElementById('remoteStreamDiv').appendChild(video);
    }
};

let sendOffer = (pc ,otherKey) => {
    console.log("sendOffer");
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


// 연결끊음
function disconnect() {

    // 채팅방에 나갔음을 서버에 알림
    stompClient.send("/send/"+room.roomId+"/leave", {},JSON.stringify(anonymous));

    stompClient.disconnect();

}


//오디오 임계치 설정
const volumeThreshold = 30;

//볼륨 모니터링 함수
function monitorVolume(stream, interval, otherKey) {
    
    // 오디오 컨텍스트 생성
    const audioContext = new (window.AudioContext || window.webkitAudioContext)();
    // 오디오 분석기 생성
    const analyser = audioContext.createAnalyser();
    // 스트림 소스 생성
    const source = audioContext.createMediaStreamSource(stream);
    // 분석기에 스트림소스 연결
    source.connect(analyser);
    // FFT(고속 푸리에 변환) 사이즈 설정 -> 사이즈가 분석의 정밀도 성능에 영향
    analyser.fftSize = 256;
    // 분석기에서 사용할 주파수 빈도의 수 계산
    const bufferLength = analyser.frequencyBinCount;
    // 주파수 빈도 데이터를 저장할 배열 생성
    // 배열의 크기는 주파수 빈도의 수와 동일
    const dataArray = new Uint8Array(bufferLength);

    // 볼륨 측정하는 내부 함수
    function measure() {
        // 분석기 사용해서 주파수 빈도 데이터를 dataArray에 저장
        analyser.getByteFrequencyData(dataArray);
        let sum = 0;
        // dataArray의 모든 값을 더함
        // 볼륨 측정을 위한 전처리 과정
        for (let i = 0; i < bufferLength; i++) {
            sum += dataArray[i];
        }
        // 평균 볼륨 계산
        let average = sum / bufferLength;
        // 평균 볼륨을 기반으로 실제 볼륨 수치 계산 (0~100)
        let volume = (Math.sqrt(average) / Math.sqrt(255)) * 100;

        let speakUser = document.getElementsByClassName(otherKey)[0];
        // 볼륨이 임계치 이상이면 사용자가 말하는 것으로 간주
        if(volume >= volumeThreshold) {
            speakUser.classList.add('speaker');
        }
        else {
            if (speakUser.classList.contains('speaker')) {
                    speakUser.classList.remove('speaker');
            }
        }

        setTimeout(measure, interval); // 설정된 간격으로 반복 측정
    }

    measure();
}




window.onload = async () =>{
    if(anonymous.role != 'judge') {
        await Promise.all([startCam(), connect()]);
        await sendKey();
    }
    else {
        await connect();
        await sendKey();
    }
}


// 페이지를 벗어나면 연결끊음
window.addEventListener('beforeunload', function (event) {
    event.preventDefault();
    event.returnValue = '';
    disconnect();
});

window.addEventListener('pagehide', function (event) {
    disconnect();
});





