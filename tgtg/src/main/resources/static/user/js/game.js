/**
 * 
 */
 let gameTimeInterval;
 function gameTimer(endTimeString){
	console.log(endTimeString);
	const [hours, minutes, seconds] = endTimeString.split(':');
	
	const endTime = new Date();
	endTime.setHours(hours);
	endTime.setMinutes(minutes);
	endTime.setSeconds(seconds.split('.')[0]);
	//endTime이 끝난 시간
	// let timer = setInterval(timeWrite(endTime), 1000);	
    gameTimeInterval = setInterval(function() {timeWrite(endTime); }, 1000);
	
	console.log(endTime.getHours()+'시 '+endTime.getMinutes() + '분에 끝남');	
}

let oneMinute = false;
let tenSecond = false;
function timeWrite(endTime){
    let alarm = document.querySelector('#digitalTimer');
    let now = new Date();
    let diff = endTime.getTime() - now.getTime();
    
    let minute = Math.floor(diff / 60000);
    let second = Math.floor((diff % 60000) / 1000);

    console.log(minute+'분 '+ second+'초');

    if(minute>=0 && second>=0){
        alarm.innerHTML = (minute <= 9 ? "0"+minute : minute)+':'+(second <= 9 ? "0"+second : second);
    }
    if(minute<=1 && second<=0&&!oneMinute){
        let div = document.createElement('div');
        div.innerText = "채팅 시간이 1분 남았습니다.";
        div.setAttribute("class", "connectAlert");
        chatView.appendChild(div);
        chatView.scrollTop = chatView.scrollHeight;
        oneMinute = !oneMinute;
    }
    if(minute <= 0 && second <= 10&&!tenSecond) {
        alarm.style.animation = 'vibration .1s cubic-bezier(0.99, -1.93, 0, 2.84) infinite';
        alarm.style.color = '#c30000';
        let div = document.createElement('div');
        div.innerText = "채팅 시간이 10초 남았습니다. 관전자들은 투표를 시작해 주십시오.";
        div.setAttribute("class", "connectAlert");
        chatView.appendChild(div);
        chatView.scrollTop = chatView.scrollHeight;
        tenSecond = !tenSecond;
        gameVote();
    }
    if(minute <= 0 && second <= 0){
        clearInterval(gameTimeInterval);
        stompClient.send("/send/"+room.roomId+"/getResult", {});
    } 
}

//관전자 게임 투표
function gameVote(){
    if(role == '심판'){
        let teamSelect;
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
                title: "“" + room.answerA + "” 선택 완료!",
                text: "선택하신 팀은 변경하실 수 없습니다.",
                icon: "success"
              });
              let teamSelect = 0;
              stompClient.send("/send/"+room.roomId+"/gameVote", {},JSON.stringify(teamSelect));
            } else if (
              /* Read more about handling dismissals below */
              result.dismiss === Swal.DismissReason.cancel
            ) {
              swal.fire({   //B팀 선택 시
                title: "“" + room.answerB + "” 선택 완료!",
                text: "선택하신 팀은 변경하실 수 없습니다.",
                icon: "success"
              });
              let teamSelect = 1;
              stompClient.send("/send/"+room.roomId+"/gameVote", {},JSON.stringify(teamSelect));
            }
        });
    }
}

function showResult(vote){
    console.log(vote);
    Swal.fire({
        title : "“" + room.answerA + "” 승리!",
        text: "선택하지 않을 경우 메인화면으로 이동합니다.",
        timer : 5000,
        timerProgressBar: true,
        showCancelButton: true,
        confirmButtonText: "메인화면",
        cancelButtonText: "새게임",
        allowOutsideClick: false
    }).then((result) => {
        if (result.isConfirmed) {
          
        } else if (
          /* Read more about handling dismissals below */
          result.dismiss === Swal.DismissReason.cancel
        ) {
          
        }
    });

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
        if(room.type == 'voice') {
            img.classList.add(list[i].key);
        }

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

            div.appendChild(img);
            div.appendChild(name);
            div.appendChild(reportDiv);
        }
        else if(room.type == 'voice') {
            let muteDiv = document.createElement('div');
            muteDiv.classList.add('muteDiv');
            let muteBtn = document.createElement('button');
            muteBtn.setAttribute('id','voiceMuteBtn');
            let muteImg = document.createElement('img');
            muteImg.setAttribute('src','/user/img/chat/mute.png');
            muteBtn.appendChild(muteImg);
            muteDiv.appendChild(muteBtn);

            muteBtn.addEventListener('click', function(){
                voiceMute();
            })

            div.appendChild(img);
            div.appendChild(name);
            div.appendChild(muteDiv);
        }

        else {
            div.appendChild(img);
            div.appendChild(name);
            div.appendChild(reportDiv);
        }



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

// 퇴장 알림
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