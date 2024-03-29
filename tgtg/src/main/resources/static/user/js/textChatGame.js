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

        // 채팅방에 접속했음을 서버에 알림
        stompClient.send("/send/" + room.roomId + "/enter", {}, JSON.stringify(anonymous));
    });
    
    Swal.fire({
            title: "주제 : "+room.answerA+" VS "+room.answerB,
            html: "회원님의 역할은 <b>"+role+"</b> 입니다",
            timer: 5000,
            timerProgressBar: true,
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading();
                // const timer = Swal.getPopup().querySelector("b");
                let timeLeft = 4; // 남은 시간 설정 (10초)
                timerInterval = setInterval(() => {
                    timer.textContent = `${timeLeft}`;
                    timeLeft--; // 시간 감소
                }, 1000); // 1초마다 실행
            },
            willClose: () => {
                clearInterval(timerInterval);
            }
            }).then((result) => {
                if (result.dismiss === Swal.DismissReason.timer) {
                    console.log('닫힘');
                }
            });
};

// 연결끊음
function disconnect() {

    // 채팅방에 나갔음을 서버에 알림
    stompClient.send("/send/" + room.roomId + "/leave", {}, JSON.stringify(anonymous));
}

// 채팅 전송
function sendChat() {
    if ($("#message").val() != "") {
        // JSON형태로 바꾸어서 보냄
        stompClient.send("/send/" + room.roomId + '/game', {},
            JSON.stringify({
                'sender': anonymous.anonymousNickname,
                'senderEmail': anonymous.anonymousId,
                'senderImage': anonymous.anonymousImage,
                'message': $("#message").val(),
                'gameRole' : anonymous.role
            }));
        $("#message").val('');
    }
}

//현재 접속자 수
function showConnectedCount(connect) {
    let connectText = document.querySelectorAll('.countConnect');
    connectText.forEach(ct => ct.innerText = connect.connectUser);

    let div = document.createElement('div');
    if (!connect.enter) {
        div.innerText = connect.anonymous.anonymousNickname + "님이 퇴장하였습니다.";
    }

    drawMemberList(connect.memberList);

    div.setAttribute("class", "connectAlert");
    chatView.appendChild(div);
    chatView.scrollTop = chatView.scrollHeight;
}

//멤버목록
function drawMemberList(list) {
    const judgeListDiv = document.getElementById('judgeList');
    const answerAListDiv = document.getElementById('answerAList');
    const answerBListDiv = document.getElementById('answerBList');
    
    while (judgeListDiv.firstChild) {
        judgeListDiv.removeChild(judgeListDiv.firstChild);
    }

    while (answerAListDiv.firstChild) {
        answerAListDiv.removeChild(answerAListDiv.firstChild);
    }

    while (answerBListDiv.firstChild) {
        answerBListDiv.removeChild(answerBListDiv.firstChild);
    }

    for (let i = 0; i < list.length; i++) {

        // 회원 감싸는 div
        let div = document.createElement('div');
        div.setAttribute("class", 'member');

        if (list[i].anonymousId == anonymous.anonymousId) {
            div.classList.add('memberList-me');
        }

        // 프로필이미지
        let img = document.createElement('img');
        img.setAttribute('src', list[i].anonymousImage);
        img.classList.add('memberListImg', 'profileImg');

        // 닉네임
        let name = document.createElement('span');
        name.classList.add('bold-font', 'memberListNickname');
        name.innerText = list[i].anonymousNickname;

        // 신고영역
        let reportDiv = document.createElement('div');
        
        if (list[i].anonymousId != anonymous.anonymousId) {
            reportDiv.classList.add('reportDiv');
            let reportBtn = document.createElement('button');
            reportBtn.classList.add('reportBtn');
            let btnImg = document.createElement('img');
            btnImg.setAttribute('src', '/user/img/chat/siren.png');
            reportBtn.appendChild(btnImg);
            reportDiv.appendChild(reportBtn);

            reportBtn.addEventListener('click', function () {
                reportMember(list[i].anonymousId, list[i].anonymousNickname);
            });
        }


        div.appendChild(img);
        div.appendChild(name);
        div.appendChild(reportDiv);

        if(list[i].role == 'answerA') {
            div.classList.add('answerA');
            answerAListDiv.appendChild(div);

        }
        else if(list[i].role == 'answerB') {
            div.classList.add('answerB');
            answerBListDiv.appendChild(div);
        }
        else if(list[i].role == 'judge'){
            div.classList.add('judge');
            judgeListDiv.appendChild(div);
        }

    }

}

// 수신된 채팅 화면에 그려주는 함수
function showChat(chatMessage) {
    let div = document.createElement('div');
    let div2 = document.createElement('div');
    div2.setAttribute("class", "divBox");
    div2.style.flex = 1;


    //프로필이미지
    let img = document.createElement('img');
    img.classList.add('profileImg','chat_'+chatMessage.gameRole);

    //프로필닉네임
    let name = document.createElement('span');
    name.setAttribute("class", "bold-font");

    //채팅내용
    let messageBox = document.createElement('div');
    messageBox.innerHTML = chatMessage.message.replace(/\n/g, "<br>");

    //프로필이미지 + 메시지
    let imgMsg = document.createElement('div');
    imgMsg.style.display = 'flex';

    let tempdiv = document.createElement('div');
    tempdiv.style.flex = 1;
    tempdiv.setAttribute("class", 'temp');

    if (chatMessage.senderEmail != anonymous.anonymousId) {
        messageBox.classList.add('box', 'other');

        messageBox.addEventListener('click', function () {
            reportChat(chatMessage.senderEmail, chatMessage.sender, chatMessage.message);
        })

        div.setAttribute('class', 'other_div');

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
        div.setAttribute('class', 'me_div');


        div2.appendChild(messageBox);

        div.appendChild(tempdiv);
        div.appendChild(div2);
    }
    chatView.appendChild(div);

    chatView.scrollTop = chatView.scrollHeight;

}

function reportMember(anonymousId, nickname) {
    Swal.fire({
        title: nickname + "님 신고",
        input: "select",
        inputOptions: {
            욕설: "욕설",
            성희롱: "성희롱",
            기타: "기타"
        },
        inputPlaceholder: "신고 카테고리를 선택해주세요.",
        showCancelButton: true,
        preConfirm: (value) => {
            if (value === "") {
                Swal.showValidationMessage("신고 카테고리를 선택해주세요."); // 유효성 검사 메시지를 보여주기
            }
        }
    }).then((result) => {
        if (result.isConfirmed && result.value) {
            // 사용자가 카테고리를 선택하고 'OK'를 누른 경우
            Swal.fire({
                title: nickname + "님을 신고하는 사유",
                input: "textarea",
                inputLabel: "상세 이유",
                inputPlaceholder: "신고 상세 사유를 입력해주세요.",
                inputAttributes: {
                    "aria-label": "Type your message here"
                },
                showCancelButton: true
            }).then((textResult) => {
                if (textResult.isConfirmed && textResult.value) {

                    fetch("/insertReport", {
                        method: "POST",
                        body: JSON.stringify({
                            reportCategory: result.value,
                            reportReason: textResult.value,
                            reportStatus: '접수',
                            reporterId: anonymous.anonymousId,
                            reportedId: anonymousId
                        }),
                        headers: {
                            "Content-Type": "application/json"
                        }
                    })
                        .then(resolve => resolve.text())
                        .then(result => {
                            if (result) {
                                Swal.fire({
                                    icon: "success",
                                    title: "신고 완료",
                                    text: nickname + "님을 신고하였습니다."
                                });
                            }
                            else {
                                Swal.fire({
                                    icon: "error",
                                    title: "신고 실패",
                                    text: "오류가 발생하였습니다."
                                });
                            }
                        })
                }
            });
        }
    });
}

//채팅 신고
function reportChat(anonymousId, nickname, message) {
    Swal.fire({
        title: nickname + "님의 채팅 신고",
        input: "select",
        text: "\"" + message + "\"",
        inputOptions: {
            욕설: "욕설",
            성희롱: "성희롱",
            기타: "기타"
        },
        inputPlaceholder: "신고 카테고리를 선택해주세요.",
        showCancelButton: true,
        preConfirm: (value) => {
            if (value === "") {
                Swal.showValidationMessage("신고 카테고리를 선택해주세요."); // 유효성 검사 메시지를 보여주기
            }
        }
    }).then((result) => {
        if (result.isConfirmed && result.value) {
            // 사용자가 카테고리를 선택하고 'OK'를 누른 경우
            Swal.fire({
                title: nickname + "님을 신고하는 사유",
                input: "textarea",
                inputLabel: "상세 이유",
                inputPlaceholder: "신고 상세 사유를 입력해주세요.",
                inputAttributes: {
                    "aria-label": "Type your message here"
                },
                showCancelButton: true
            }).then((textResult) => {
                if (textResult.isConfirmed && textResult.value) {

                    fetch("/insertReport", {
                        method: "POST",
                        body: JSON.stringify({
                            reportCategory: result.value,
                            reportReason: textResult.value,
                            reportStatus: '접수',
                            reporterId: anonymous.anonymousId,
                            reportedId: anonymousId,
                            reportChat: message
                        }),
                        headers: {
                            "Content-Type": "application/json"
                        }
                    })
                        .then(resolve => resolve.text())
                        .then(result => {
                            if (result) {
                                Swal.fire({
                                    icon: "success",
                                    title: "신고 완료",
                                    text: nickname + "님을 신고하였습니다."
                                });
                            }
                            else {
                                Swal.fire({
                                    icon: "error",
                                    title: "신고 실패",
                                    text: "오류가 발생하였습니다."
                                });
                            }
                        })
                }
            });
        }
    });

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
let startTime = Date.now(); // 타이머 시작 시간
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

let timerInterval = setInterval(gameTimer, 1000); // 1초마다 timer 함수를 호출

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