
// 메시지 전송 버튼 이벤트
document.querySelector('#msgBtn').addEventListener("click", sendChat);

//게임 준비 버튼 이벤트
document.querySelector('#readyBtn').addEventListener("click", updateReady);

//준비 상태
let isReady = false;

// 소켓 연결
function connect() {

    // 소켓 생성해서 연결시킴
    let socket = new SockJS('/ws-stomp');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
        console.log('Connected: ' );

        //해당 채팅방 구독
        stompClient.subscribe('/room/'+room.roomId, function (chatMessage) {
            // 채팅 수신되면 화면에 그려줌
            showChat(JSON.parse(chatMessage.body));
        });

        stompClient.subscribe('/room/'+room.roomId+'/getReady', function (ready) {
            showReady(JSON.parse(ready.body));
        });

        // 준비한 사용자의 수 요청
        stompClient.send("/send/"+room.roomId+"/getReady", {});

        // 채팅방에 접속했음을 서버에 알림
        stompClient.send("/send/"+room.roomId+"/enter", {});

        stompClient.subscribe('/room/'+room.roomId+'/connectedCount', function (connectedCount) {
            showConnectedCount(JSON.parse(connectedCount.body));
        });
    });  
};

// 연결끊음
function disconnect() {

    // 채팅방에서 나갔음을 서버에 알림
    stompClient.send("/send/"+room.roomId+"/leave", {});
    // if(isReady) {
    //     stompClient.send("/send/"+room.roomId+"/unready", {});
    // }
    stompClient.disconnect();

}

// 채팅 전송
function sendChat() {
    if ($("#message").val() != "") {
        // JSON형태로 바꾸어서 보냄
        stompClient.send("/send/"+room.roomId, {},
            JSON.stringify({
                'sender': sender,
                'senderEmail': senderEmail,
                'message' : $("#message").val()
            }));
        $("#message").val('');
    }
}

function updateReady() {
    // isReady = !isReady; // 상태를 반전

    if (!isReady) {
        sendReady(); // 준비 상태로 변경되면 서버에 알림
    } else {
        cancelReady(); // 준비 취소 상태로 변경되면 서버에 알림
    }

}

function updateReadyButton() {
    if (isReady) {
        readyBtn.innerHTML = '&#128148;'; // 준비 상태일 때는 정지 아이콘으로 변경
    } else {
        readyBtn.innerHTML = '&#10084;'; // 준비하지 않은 상태일 때는 재생 아이콘으로 변경
    }
}

//게임 준비
function sendReady() {
    stompClient.send("/send/"+room.roomId+"/ready", {});
}

//준비 취소
function cancelReady() {
    stompClient.send("/send/"+room.roomId+"/unready", {});
}

//현재 접속자 수
function showConnectedCount(connectCount) {
    let connect = document.querySelector('#countConnect');
    connect.innerText = connectCount;
}


//준비한 사용자
function showReady(ready) {
    console.log(ready);
    isReady = ready;
    updateReadyButton();
}

// 수신된 채팅 화면에 그려주는 함수
function showChat(chatMessage) {
    if(chatMessage.senderEmail != senderEmail) {
        let div = document.createElement('div');

        let divbox = document.createElement('div');
        divbox.classList.add('box', 'other');
        divbox.innerText = chatMessage.message;

        div.appendChild(divbox);

        let name = document.createElement('span');
        name.innerHTML = '상대방';
        div.appendChild(name);

        div.setAttribute('class','other_div');
        
        chatBox.appendChild(div);
            console.log(chatMessage.sender + " : " + chatMessage.message);
    }

    else {
        let div = document.createElement('div');
        
        let divbox = document.createElement('div');
        divbox.classList.add('box', 'me');

        divbox.innerText = chatMessage.message;
        div.appendChild(divbox);
        div.setAttribute('class','me_div');

        let name = document.createElement('span');
        name.innerHTML = '나';
        div.appendChild(name);
    
        chatBox.appendChild(div);
            console.log(chatMessage.sender + " : " + chatMessage.message);
    }
}

//창 키면 바로 연결
window.onload = function (){
    connect();
}

// 페이지를 벗어나면 연결끊음
window.addEventListener('beforeunload', function (event) {
    disconnect();
});
