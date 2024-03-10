/**
 * textChatGame.js
 */
 
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
    });  
};

// 연결끊음
function disconnect() {

    // 채팅방에 나갔음을 서버에 알림
    stompClient.send("/send/"+room.roomId+"/leave", {}, 
        JSON.stringify({
            'anonymousNickname' : anonymous.anonymousNickname,
            'anonymousImage' : anonymous.anonymousImage
        })
    );
}

// 채팅 전송
function sendChat() {
    if ($("#message").val() != "") {
        // JSON형태로 바꾸어서 보냄
        stompClient.send("/send/"+room.roomId, {},
            JSON.stringify({
                'sender': anonymous.anonymousNickname,
                'senderEmail': senderEmail,
                'senderImage': anonymous.anonymousImage,
                'message' : $("#message").val()
            }));
        $("#message").val('');
    }
}

// 수신된 채팅 화면에 그려주는 함수
function showChat(chatMessage) {
    let div = document.createElement('div');
    let div2 = document.createElement('div');
    div2.setAttribute("class","divBox");
    div2.style.flex = 1;


    //프로필이미지
    let img = document.createElement('img');
    img.setAttribute("class","profileImg");

    //프로필닉네임
    let name = document.createElement('span');
    name.setAttribute("class","bold-font");

    //채팅내용
    let messageBox = document.createElement('div');
    messageBox.innerHTML = chatMessage.message.replace(/\n/g, "<br>");

    //프로필이미지 + 메시지
    let imgMsg = document.createElement('div');
    imgMsg.style.display = 'flex';

    let tempdiv = document.createElement('div');
    tempdiv.style.flex = 1;
    tempdiv.setAttribute("class",'temp');

    if(chatMessage.senderEmail != senderEmail) {

        messageBox.classList.add('box', 'other');
        div.setAttribute('class','other_div');

        name.innerHTML = chatMessage.sender;
        img.setAttribute("src", chatMessage.senderImage);

        imgMsg.appendChild(img);
        imgMsg.appendChild(messageBox);

        div2.appendChild(name);
        div2.appendChild(imgMsg);

        div.appendChild(div2);
        div.appendChild(tempdiv);

    }

    else {
        
        messageBox.classList.add('box', 'me');
        div.setAttribute('class','me_div');

        
        div2.appendChild(messageBox);

        div.appendChild(tempdiv);
        div.appendChild(div2);
    }
    chatView.appendChild(div);

    chatView.scrollTop = chatView.scrollHeight;

}

//창 키면 바로 연결
window.onload = function (){
    connect();
}

// 페이지를 벗어나면 연결끊음
window.addEventListener('beforeunload', function (event) {
    disconnect();
}); 
 
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
			let div = document.createElement('div');
			div.innerText = "채팅 시간이 1분 남았습니다.";
			div.setAttribute("class", "connectAlert");
		    chatView.appendChild(div);
		    chatView.scrollTop = chatView.scrollHeight;
		} else if(time == 10){
			let div = document.createElement('div');
			div.innerText = "채팅 시간이 10초 남았습니다. 관전자들은 투표를 시작해 주십시오.";
			div.setAttribute("class", "connectAlert");
		    chatView.appendChild(div);
		    chatView.scrollTop = chatView.scrollHeight;
		}
	}
}, 1000);

//새로고침 방지
/*function NotReload(){
    if( (event.ctrlKey == true && (event.keyCode == 78 || event.keyCode == 82)) || (event.keyCode == 116) ) {
        event.keyCode = 0;
        event.cancelBubble = true;
        event.returnValue = false;
    } 
}*/
document.onkeydown = NotReload;