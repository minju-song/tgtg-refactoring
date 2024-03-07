/**
 * textChatGame.js
 */
 
// 메시지 전송 버튼 이벤트
let gameRole = "";
let msgBtn = document.querySelectorAll('#msgBtn');
for(let i=0; i<msgBtn.length; i++){
    msgBtn[i].addEventListener("click", function(){
        if(i == 0){
            gameRole = "game";
            sendChat(msgBtn[i]);
        }else{
            gameRole = "watch";
            sendChat(msgBtn[i]);
        }
    });
}

//엔터키
let chatSendButton = document.querySelectorAll('#chatSend');
for(let i=0; i<chatSendButton.length; i++){
    chatSendButton[i].onkeypress = function(e){
        if (e.keyCode === 13) {
            if(i == 0){
                gameRole = "game";
                sendChat(chatSendButton[i].querySelector('#msgBtn'));
            }else{
                gameRole = "watch";
                sendChat(chatSendButton[i].querySelector('#msgBtn'));
            }
        }
    }
}

// 소켓 연결
function connect() {

    // 소켓 생성해서 연결시킴
    let socket = new SockJS('/ws-stomp');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
        console.log('Connected: ' );

        //해당 채팅방 구독
        stompClient.subscribe('/room/'+room.roomId+'/game', function (chatMessage) {
            // 채팅 수신되면 화면에 그려줌
            showChat(JSON.parse(chatMessage.body));
        });
    });  
};

// 채팅 전송
function sendChat(Btn, num) {
    console.log(Btn)
    if(Btn.previousElementSibling.value != ''){
        stompClient.send("/send/"+room.roomId+"/game", {},
            JSON.stringify({
                'sender': sender,
                'senderEmail': senderEmail,
                'message' : Btn.previousElementSibling.value,
                gameRole
            }));
        $("#message").val('');
    }
}

// 수신된 채팅 화면에 그려주는 함수
function showChat(chatMessage) {
    console.log(chatMessage.gameRole)
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
        if(gameRole == 'game'){
            gameChatBox.appendChild(div);
        }else{
            watchChatBox.appendChild(div);
        }
            //console.log(chatMessage.sender + " : " + chatMessage.message);
    } else {
        let div = document.createElement('div');
        
        let divbox = document.createElement('div');
        divbox.classList.add('box', 'me');

        divbox.innerText = chatMessage.message;
        div.appendChild(divbox);
        div.setAttribute('class','me_div');

        let name = document.createElement('span');
        name.innerHTML = '나';
        div.appendChild(name);
        if(gameRole == 'game'){
            gameChatBox.appendChild(div);
        }else{
            watchChatBox.appendChild(div);
        }
            //console.log(chatMessage.sender + " : " + chatMessage.message);
    }
}

//창 키면 바로 연결
window.onload = function (){
    connect();
}

//5분 게임 타이머
const remainingMin = document.getElementById("remaining__min");
const remainingSec = document.getElementById("remaining__sec");

let time = 300;
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