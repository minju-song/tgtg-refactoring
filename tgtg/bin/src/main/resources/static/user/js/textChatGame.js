/**
 * textChatGame.js
 */
 
// 메시지 전송 버튼 이벤트
document.querySelector('#msgBtn').addEventListener("click", sendChat);

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

//10분 게임 타이머
const remainingMin = document.getElementById("remaining__min");
const remainingSec = document.getElementById("remaining__sec");

let time = 600;
setInterval(function () {
	if (time > 0) { // >= 0 으로하면 -1까지 출력된다.
		time = time - 1;
		let min = "0" + Math.floor(time / 60);
		let sec = String(time % 60).padStart(2, "0");
		remainingMin.innerText = min;
		remainingSec.innerText = sec;
		if(time == 60){
			document.querySelector('#gameAlarm').innerHTML = '" 채팅 시간이 1분 남았습니다. "';
		} else if(time == 10){
			document.querySelector('#gameAlarm').innerHTML = 
			`" 채팅 시간이 10초 남았습니다.
			관전자들은 투표를 시작해 주십시오. "`;
		}
	}
}, 1000);
  
//엔터키
$('#chatSend').keypress(function (e) {
    if (e.keyCode === 13) {
        sendChat();
    }
});

if(time == 580){
	console.log("ddddddddd");
} else if(time == 10){
	
}