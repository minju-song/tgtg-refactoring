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

function timeWrite(endTime){
    let alarm = document.querySelector('#timer');
    let now = new Date();
    let diff = endTime.getTime() - now.getTime();
    
    let minute = Math.floor(diff / 60000);
    let second = Math.floor((diff % 60000) / 1000);

    console.log(minute+'분 '+ second+'초');
    alarm.innerHTML = (minute <= 9 ? "0"+minute : minute)+':'+(second <= 9 ? "0"+second : second);
    if(minute <= 0 && second <= 10) {
        alarm.style.animation = 'vibration .1s cubic-bezier(0.99, -1.93, 0, 2.84) infinite';
        alarm.style.color = '#c30000';
    }
    if(minute <= 0 && second <= 0){
        clearInterval(gameTimeInterval);
    } 
}