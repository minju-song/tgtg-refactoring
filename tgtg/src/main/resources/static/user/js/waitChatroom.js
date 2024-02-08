
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
        stompClient.subscribe('/room/'+roomId, function (chatMessage) {
            // 채팅 수신되면 화면에 그려줌
            showChat(JSON.parse(chatMessage.body));
        });
    });  
};

// 연결끊음
function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

// 채팅 전송
function sendChat() {
    if ($("#message").val() != "") {
        // JSON형태로 바꾸어서 보냄
        stompClient.send("/send/"+roomId, {},
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
window.BeforeUnloadEvent = function (){
    disconnect();
}
