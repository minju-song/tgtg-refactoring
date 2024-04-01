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
    let socket = new SockJS('/ws-stomp');
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

//5분 게임 타이머
/*let startTime = Date.now(); // 타이머 시작 시간
let totalSeconds = 305; // 총 시간을 초로 계산 (5분)

let remainingMin = document.getElementById("remaining__min"); // 분을 표시할 요소
let remainingSec = document.getElementById("remaining__sec"); // 초를 표시할 요소

function gameTimer() {
    let elapsedTime = Math.trunc((Date.now() - startTime) / 1000); // 경과 시간을 초 단위로 계산
    let remainingTime = totalSeconds - elapsedTime; // 남은 시간을 초 단위로 계산

    if (remainingTime >= 0) {
        let minutes = Math.floor(remainingTime / 60); // 남은 분
        let seconds = remainingTime % 60; // 남은 초

        // 남은 시간을 화면에 표시
        remainingMin.textContent = minutes.toString().padStart(2, '0');
        remainingSec.textContent = seconds.toString().padStart(2, '0');
        if(remainingTime == 300){
            document.querySelector('.timerSircle').style.display = 'block';
        }
        else if(remainingTime == 60){
            let div = document.createElement('div');
            div.innerText = "채팅 시간이 1분 남았습니다.";
            div.setAttribute("class", "connectAlert");
            chatView.appendChild(div);
            chatView.scrollTop = chatView.scrollHeight;
        } else if(remainingTime == 10){
            let div = document.createElement('div');
            div.innerText = "채팅 시간이 10초 남았습니다. 관전자들은 투표를 시작해 주십시오.";
            div.setAttribute("class", "connectAlert");
            chatView.appendChild(div);
            chatView.scrollTop = chatView.scrollHeight;
            document.querySelector('.timerSircle').style.animation = 'vibration .1s cubic-bezier(0.99, -1.93, 0, 2.84) infinite';
            gameVote();
        }
    } else {
        // 타이머가 끝났을 때의 로직
        console.log("타이머 종료");
        clearInterval(timerInterval); // 타이머 중지
    }
}

let timerInterval = setInterval(gameTimer, 1000); // 1초마다 timer 함수를 호출*/

//관전자 게임 투표
function gameVote(){
    if(role == '심판'){
        Swal.fire({
            title : "투표를 진행해 주세요.",
            text: "선택하지 않을 경우 투표는 무효처리 됩니다!",
            timer : 10000,
            timerProgressBar: true,
            showCancelButton: true,
            confirmButtonColor: "#F66868",
            cancelButtonColor: "#687FF6",
            confirmButtonText: room.answerA,
            cancelButtonText: room.answerB,
            allowOutsideClick: false
        }).then((result) => {
            if (result.isConfirmed) {
              swal.fire({   //A팀 선택 시
                title: "A팀 선택 완료!",
                text: "선택하신 팀은 변경하실 수 없습니다.",
                icon: "success"
              });
            } else if (
              /* Read more about handling dismissals below */
              result.dismiss === Swal.DismissReason.cancel
            ) {
              swal.fire({   //B팀 선택 시
                title: "B팀 선택 완료!",
                text: "선택하신 팀은 변경하실 수 없습니다.",
                icon: "success"
              });
            }
        });
    }
}

//새로고침 방지
/*function NotReload(){
    if( (event.ctrlKey == true && (event.keyCode == 78 || event.keyCode == 82)) || (event.keyCode == 116) ) {
        event.keyCode = 0;
        event.cancelBubble = true;
        event.returnValue = false;
    } 
}
document.onkeydown = NotReload;*/