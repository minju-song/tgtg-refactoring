// 메시지 전송 버튼 이벤트
document.querySelector('#sendBtn').addEventListener("click", sendChat);



//이모지토스트 활성화 / 비활성화
let emojiToast = document.getElementById('emojiToast');

document.querySelector('#emojiBtn').addEventListener("click", function(e) {
    emojiToast.classList.add('active');
});
document.querySelector('#emojiClose').addEventListener("click", function(e) {
    emojiToast.classList.remove('active'); 
})



//메시지 input
let msgInput = document.getElementById('message');

let emojis = document.querySelectorAll('.emoji');

emojis.forEach(function(emoji) {
    emoji.addEventListener('click', function(e){
        console.log(this.textContent + "클릭");
        msgInput.value += this.textContent;
    })
})

// 엔터키 누르면 전송
msgInput.onkeyup = function(e) {
    if(e.keyCode == 13 ) {
        sendChat();
    }
    else {
        return;
    }
}