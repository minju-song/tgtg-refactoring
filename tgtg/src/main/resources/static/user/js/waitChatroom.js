


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
        stompClient.send("/send/"+room.roomId+"/enter", {}, 
            JSON.stringify({
                'anonymousNickname' : anonymous.anonymousNickname,
                'anonymousImage' : anonymous.anonymousImage
            })
        );


        // 준비한 사용자의 수 요청
        stompClient.send("/send/"+room.roomId+"/getReadyCount", {});
        

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
                'sender': anonymous.anonymousNickname,
                'senderEmail': senderEmail,
                'senderImage': anonymous.anonymousImage,
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
        readyBtn.innerText = 'CANCEL';
        readyBtn.classList.replace('unreadyStatus', 'readyStatus');
         // 준비 상태일 때는 정지 아이콘으로 변경
    } else {
        readyBtn.innerText = 'READY'; // 준비하지 않은 상태일 때는 재생 아이콘으로 변경
        readyBtn.classList.replace('readyStatus', 'unreadyStatus');
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
function showConnectedCount(connect) {
    // console.log(connectCount);
    let connectText = document.querySelector('#countConnect');
    connectText.innerText = connect.connectUser;

    let div = document.createElement('div');
    if(connect.enter) {
        div.innerText = connect.nickname+"님이 입장하였습니다.";
    }
    else {
        div.innerText = connect.nickname+"님이 퇴장하였습니다.";
    }
    div.setAttribute("class", "connectAlert");
    chatView.appendChild(div);
    chatView.scrollTop = chatView.scrollHeight;
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
