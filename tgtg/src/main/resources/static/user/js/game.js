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
    let alarm = document.querySelector('#timer');
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
}