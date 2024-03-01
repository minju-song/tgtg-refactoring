
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

        stompClient.subscribe('/room/'+room.roomId+'/connectedCount', function (connectedCount) {
            showConnectedCount(JSON.parse(connectedCount.body));
        });

        stompClient.subscribe('/room/'+room.roomId+'/getReady', function (ready) {
            showReady(JSON.parse(ready.body));
        });

        stompClient.subscribe('/room/'+room.roomId+'/startGame', function (start) {
            startGame(start.body);
        });

        
        // 채팅방에 접속했음을 서버에 알림
        stompClient.send("/send/"+room.roomId+"/enter", {});

        // 준비한 사용자의 수 요청
        stompClient.send("/send/"+room.roomId+"/getReadyCount", {});
        

    });  
};

// 연결끊음
function disconnect() {

    // 채팅방에서 나갔음을 서버에 알림
    stompClient.send("/send/"+room.roomId+"/leave", {});
    if(isReady) {
        stompClient.send("/send/"+room.roomId+"/unready", {});
    }
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


//상태 업데이트
function updateReady() {
    isReady = !isReady; // 상태를 반전

    if (isReady) {
        sendReady(); // 준비 상태로 변경되면 서버에 알림
    } else {
        cancelReady(); // 준비 취소 상태로 변경되면 서버에 알림
    }

}

//버튼 업데이트
function updateReadyButton() {
    if (isReady) {
        readyBtn.innerText = 'CANCEL'; // 준비 상태일 때는 정지 아이콘으로 변경
    } else {
        readyBtn.innerText = 'READY'; // 준비하지 않은 상태일 때는 재생 아이콘으로 변경
    }
}

//게임 준비
function sendReady() {
    stompClient.send("/send/"+room.roomId+"/ready", {});
    updateReadyButton();
}

//준비 취소
function cancelReady() {
    stompClient.send("/send/"+room.roomId+"/unready", {});
    updateReadyButton();
}

//게임 시작 함수
function startGame(start) {
    console.log("시작 : "+start);
    let timeLeft = 10;
    const timerElement = document.getElementById('timer'); // 타이머를 표시할 요소

    timerElement.style.display = 'flex'; // 타이머 활성화

    const timerId = setInterval(function() {
        if (timeLeft <= 0) {
            clearInterval(timerId); // 타이머 종료
            timerElement.innerText = "START";
            window.location.href = '/user/textGame';// 타이머 숨기기
        } else {
            timerElement.innerText = timeLeft; // 화면 갱신
            timeLeft--; // 시간 감소
        }
    }, 1000);

}

//현재 접속자 수
function showConnectedCount(connectCount) {
    let connect = document.querySelector('#countConnect');
    connect.innerText = connectCount;
}


//준비한 사용자
function showReady(ready) {
    console.log("수신값:"+ready);
    let connect = document.querySelector('#countReady');
    connect.innerText = ready;
    // updateReadyButton();
}

// 수신된 채팅 화면에 그려주는 함수
function showChat(chatMessage) {
    let div = document.createElement('div');
    let divbox = document.createElement('div');

    divbox.innerText = chatMessage.message;
  

    let name = document.createElement('span');
    name.setAttribute("class","bold-font");

    if(chatMessage.senderEmail != senderEmail) {

        divbox.classList.add('box', 'other', 'bold-font' );
        div.setAttribute('class','other_div');

        name.innerHTML = chatMessage.sender;

        div.appendChild(name);
        div.appendChild(divbox);

    }

    else {
        
        divbox.classList.add('box', 'me', 'bold-font');
        div.setAttribute('class','me_div');

        name.innerHTML = '나';

        div.appendChild(divbox);
        div.appendChild(name);

    }
    chatBox.appendChild(div);

    

    console.log(chatMessage.sender + " : " + chatMessage.message);
}

//창 키면 바로 연결
window.onload = function (){
    connect();
}

// 페이지를 벗어나면 연결끊음
window.addEventListener('beforeunload', function (event) {
    disconnect();
});
