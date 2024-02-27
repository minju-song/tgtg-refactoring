/**
 * 익명관리 스크립트
 */

"use strict";

document.addEventListener("DOMContentLoaded", (event) => {
    // 닉네임 목록 이벤트
    document.querySelectorAll(".nickname-list li").forEach((li) => {
        li.addEventListener("click", selectNickname);
    })
    
    // 체크박스 선택 이벤트
	document.querySelectorAll("input[type='checkbox']").forEach((input) => {
		input.addEventListener("change", showDeleteButton);
	})
	
	// 삭제버튼 이벤트
	document.querySelector(".delete-nickname").addEventListener("click", deleteNickname);

	// 수정버튼 이벤트
	document.querySelector(".update-button").addEventListener("click", activateInput);
	
	// 취소버튼 이벤트
	document.querySelector(".cancle-button").addEventListener("click", cancleUpdate);
	
	// 첨부버튼 이벤트
	document.querySelector(".add-file button").addEventListener("click", clickFileInput);
	document.querySelector(".profile-image input").addEventListener("change", setFileName);
})

function selectNickname(event) {
	const nicknameList = document.querySelectorAll(".nickname-list li");
    let classList = event.currentTarget.classList;

	if(event.target.tagName !== "INPUT") {
		nicknameList.forEach((li) => {
	        if(li === event.currentTarget) {
	        	// 1. 선택된 요소 강조
	            li.classList.add("selected");
	            
	            // 2. 선택된 요소의 정보 바인딩
	            const id = event.currentTarget.dataset.id;
	            bindSelectedInfo(id);
	            
	        } else {
	            li.classList.remove("selected");
	        }
	    })
	}
}

function bindSelectedInfo(id) {
	// 해당 id의 닉네임 정보 상세보기 영역이 바인딩 시키기(data-id 수정 버튼에 저장)
	console.log(id);
	document.querySelector(".update-button").dataset.id = id;
}

function showDeleteButton() {
	let isShow = false;
	const deleteButton = document.querySelector(".delete-nickname");
	
	document.querySelectorAll("input[type='checkbox']").forEach((input) => {
		if(input.checked) {
			isShow = true;
			return;
		}
	})
	
	if(isShow) {
		deleteButton.classList.add("show");
	} else {
		deleteButton.classList.remove("show");
	}
}

function deleteNickname() {
	const deleteList = document.querySelectorAll("input[type='checkbox']:checked");

	// 체크된 목록 삭제하기
	console.log("delete list!");
	console.log(deleteList);
}

let nickName;
let fileName;
function activateInput() {
	// css 수정
	document.querySelector(".add-file").style.background = "#fff";
	document.querySelector(".nickname input").style.background = "#fff";
	document.querySelector(".add-file button").style.background = "#5D87FF";
	
	// input 활성화
	document.querySelector(".nickname input").disabled = false;
	document.querySelector(".nickname input").readOnly = false;
	document.querySelector(".profile-image input").disabled = false;

	// 버튼 활성화
	document.querySelector(".update-button").style.display = "none";
	document.querySelector(".save-button").style.display = "block";
	document.querySelector(".cancle-button").style.display = "block";
	
	// id정보 버튼에 저장
	const id = document.querySelector(".update-button").dataset.id;
	document.querySelector(".save-button").dataset.id = id;
	document.querySelector(".cancle-button").dataset.id = id;
	
	nickName = document.querySelector(".nickname input").value;
	fileName = document.querySelector(".add-file span").textContent;
}

function cancleUpdate() {
	// css 수정
	document.querySelector(".add-file").style.background = "#eaeff4";
	document.querySelector(".nickname input").style.background = "#eaeff4";
	document.querySelector(".add-file button").style.background = "#949494";
	
	// input 비활성화
	document.querySelector(".nickname input").disabled = true;
	document.querySelector(".nickname input").readOnly = true;
	document.querySelector(".profile-image input").disabled = true;

	// 버튼 활성화
	document.querySelector(".update-button").style.display = "block";
	document.querySelector(".save-button").style.display = "none";
	document.querySelector(".cancle-button").style.display = "none";
	
	// 원래이름 세팅
	document.querySelector(".nickname input").value = nickName;
	document.querySelector(".add-file span").textContent = fileName;
	document.querySelector(".profile-image input").value = "";
}

function clickFileInput() {
	document.querySelector(".profile-image input").click();
}

function setFileName(event) {
	const fileName = document.querySelector(".profile-image input").files[0].name;
	event.target.closest(".profile-image").querySelector(".add-file span").textContent = fileName;
}