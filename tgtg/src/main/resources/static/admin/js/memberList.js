/**
 * 회원목록 조회 스크립트 파일
 */
"use strict";

const Toast = Swal.mixin({
    toast: true,
    position: 'top',
    showConfirmButton: false,
    timer: 2000,
    timerProgressBar: true,
    didOpen: (toast) => {
        toast.addEventListener('mouseenter', Swal.stopTimer)
        toast.addEventListener('mouseleave', Swal.resumeTimer)
    }
})

document.addEventListener("DOMContentLoaded", (event) => {
    // 검색버튼 클릭 이벤트
    const searchButton = document.querySelector(".search-button");
    searchButton.addEventListener("click", searchList);

    // 검색 엔터 이벤트
    const searchInput = document.querySelector("input[name='memberId']");
    searchInput.addEventListener("keydown", function(event) {
        if(event.key === "Enter") searchList();
    })

    // 검색항목(select box) 변경 이벤트
    const searchCategory = document.querySelector("select#searchCategory");
    searchCategory.addEventListener("change", changeSearchArea);

    // 계정정지/정지해제 버튼 클릭 이벤트
    const stopButtons = document.querySelectorAll(".stop-button");
    stopButtons.forEach((button) => {
        button.addEventListener("click", updateMemberStop);
    })

})

// 검색 함수
function searchList() {
    const searchCategory = document.querySelector("select#searchCategory").value;
    if(searchCategory === "memberId") {
        const memberId = document.querySelector("input[name='memberId']").value;
        location.href = "/admin/memberList?memberId=" + memberId;
    }
    if(searchCategory === "memberStop") {
        const memberStop = document.querySelector("input[type='radio']:checked");

        if(memberStop === null) {
            Toast.fire({
                icon: "warning",
                title: "계정정지 유무를 선택해주세요."
            })
        } else {
            const memberStopValue = memberStop.value;
            location.href = "/admin/memberList?memberStop=" + memberStopValue;
        }
    }
}

// 검색항목 변경시 검색옵션 변경 함수
function changeSearchArea() {
    const searchCategory = document.querySelector("select#searchCategory").value;
    const searchInputClassList = document.querySelector("input[name='memberId']").classList;
    const searchOptionClassList = document.querySelector(".member-stop").classList;

    if(searchCategory === "memberId") {
        if(searchInputClassList.contains("d-none")) {
            searchInputClassList.remove("d-none");
            searchInputClassList.add("d-block");
            searchOptionClassList.remove("d-block");
            searchOptionClassList.add("d-none");
        }
    }
    if(searchCategory === "memberStop") {
        if(searchInputClassList.contains("d-block")) {
            searchInputClassList.remove("d-block");
            searchInputClassList.add("d-none");
            searchOptionClassList.remove("d-none");
            searchOptionClassList.add("d-block");
        }
    }
}

// 회원 계정정지 정보 업데이트 함수
function updateMemberStop(event) {
    let stopYn = event.target.innerText;
    const memberId = event.target.closest("tr").firstElementChild.innerText;

    if(stopYn === "계정정지") {
        stopYn = "stop";        
    } else if(stopYn === "정지해제") {
        stopYn = "active";
    }

    fetch("/admin/member/update/memberStop", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            stopYn: stopYn,
            memberId: memberId,
        }),
    })
    .then((response) => response.json())
    .then((result) => {
        if(result > 0) {
            Swal.fire({
                icon: "success",
                text: "정상처리 되었습니다!",
                confirmButtonText: "확인",
            }).then((result) => {
                if (result.isConfirmed) {
                    location.reload(true);
                }
            });
        } else {
            Toast.fire({
                icon: "error",
                title: "죄송합니다. 처리 중 오류가 발생했습니다.\n다시 시도 부탁드립니다."
            })
        }
    });
}