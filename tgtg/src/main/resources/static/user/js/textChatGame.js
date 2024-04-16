/**
 * textChatGame.js
 */

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

// 소켓 연결
function connect() {

    // 소켓 생성해서 연결시킴
    let socket = new SockJS('https://localhost:8099/ws-stomp');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
        console.log('Connected: ');

        //해당 채팅방 구독
        stompClient.subscribe('/room/' + room.roomId + '/game', function (chatMessage) {

            // 채팅 수신되면 화면에 그려줌
            showChat(JSON.parse(chatMessage.body));
        });

        //연결된 사용자의 수
        stompClient.subscribe('/room/' + room.roomId + '/connect', function (connectedCount) {
            showConnectedCount(JSON.parse(connectedCount.body));
        });
        
        //타이머 현재시간
        stompClient.subscribe('/room/' + room.roomId + '/sendTime', function (endTime) {
            gameTimer(JSON.parse(endTime.body));
        });

        //심판 투표 결과
        stompClient.subscribe('/room/'+room.roomId+'/getResult', function (vote) {
            showResult(vote.body);
        });

        // 채팅방에 접속했음을 서버에 알림
        stompClient.send("/send/" + room.roomId + "/enter", {}, JSON.stringify(anonymous));
    });
    
    Swal.fire({
            title: room.answerA+"<br> VS <br>"+room.answerB,
            html: "회원님의 역할은 <b>"+role+"</b> 입니다",
            timer: 5000,
            timerProgressBar: true,
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading();
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
                    //타이머 시작 시간 등록
                    stompClient.send("/send/" + room.roomId + "/sendTime", {});
                }
            });
};

// 연결끊음
function disconnect() {

    // 채팅방에 나갔음을 서버에 알림
    stompClient.send("/send/" + room.roomId + "/leave", {}, JSON.stringify(anonymous));
}


//창 키면 바로 연결
window.onload = function () {
    connect();
}

// 페이지를 벗어나면 연결끊음
window.addEventListener('beforeunload', function (event) {
    disconnect();
});

//새로고침 방지
/*function NotReload(){
    if( (event.ctrlKey == true && (event.keyCode == 78 || event.keyCode == 82)) || (event.keyCode == 116) ) {
        event.keyCode = 0;
        event.cancelBubble = true;
        event.returnValue = false;
    } 
}
document.onkeydown = NotReload;*/