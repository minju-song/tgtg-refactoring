// 메시지 전송 버튼 이벤트
document.querySelector('#sendBtn').addEventListener("click", sendChat);

function reportMember(anonymousId, nickname) {
    Swal.fire({
        title: nickname+"님 신고",
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
                title: nickname+"님을 신고하는 사유",
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
                            reportCategory : result.value,
                            reportReason : textResult.value,
                            reportStatus : '접수',
                            reporterId : anonymous.anonymousId,
                            reportedId : anonymousId 
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
                                text: nickname+"님을 신고하였습니다."
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

