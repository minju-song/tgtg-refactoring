/**
 * 익명관리 스크립트
 */

"use strict";

document.addEventListener("DOMContentLoaded", (event) => {

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
	
	document.querySelector(".nickname-list li").classList.add("selected");

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
	document.querySelector(".nickname-detail-area .cancle-button").addEventListener("click", cancleUpdate);
	
	// 첨부버튼 이벤트
	document.querySelectorAll(".add-file button").forEach((button) => {
		button.addEventListener("click", (event) => {
			event.target.closest(".profile-image").querySelector("input[type='file']").click();
		});
	})
	document.querySelectorAll(".profile-image input").forEach((input) => {
		input.addEventListener("change", setFileName);
	})

	// 닉네임 추가 버튼 이벤트
	document.querySelector(".nickname-add").addEventListener("click", () => {
		document.querySelector(".nickname-add-area").classList.add("show-area");
		document.querySelector(".nickname-detail-area").classList.remove("show-area");
	});

	// 추가버튼 이벤트
	document.querySelector(".add-button").addEventListener("click", addNickname);

	// 추가 취소 버튼 이벤트
	document.querySelector(".nickname-add-area .cancle-button").addEventListener("click", cancleAdd);


	function selectNickname(event) {
		if(event.target.tagName !== "INPUT") {
			document.querySelector(".nickname-add-area").classList.remove("show-area");
			document.querySelector(".nickname-detail-area").classList.add("show-area");

			// 1. 이전 선택된 요소 .selected remove
			document.querySelector(".nickname-list .selected").classList.remove("selected");

			// 2. 현재 선택된 요소 .selected add, id 정보 상세보기 함수에 넘겨주기
			event.currentTarget.classList.add("selected");
			bindSelectedInfo(event.currentTarget.dataset.id);
		}
	}
	
	function bindSelectedInfo(id) {
		// 해당 id의 닉네임 정보 상세보기 영역이 바인딩 시키기(data-id 수정 버튼에 저장)
		document.querySelector(".update-button").dataset.id = id;
		
		fetch("/management/anonymousInfo?anonymousId=" + id)
			.then((response) => response.json())
		  	.then((data) => {
		  		const detailArea = document.querySelector(".nickname-detail-area");
		  		detailArea.querySelector(".profile-image img").setAttribute("src", data.anonymousImage);
		  		detailArea.querySelector(".add-file span").textContent = data.anonymousImageName;
		  		detailArea.querySelector(".nickname input").value = data.anonymousNickname;
		  	})
		  	.catch((error) => console.log("error:", error));
		
	}
	
	function showDeleteButton() {
		const deleteButton = document.querySelector(".delete-nickname");
		const isShow = document.querySelector("input[type='checkbox']:checked");

		if(isShow) deleteButton.classList.add("show");
		else deleteButton.classList.remove("show");
	}
	
	function deleteNickname() {
		const deleteList = document.querySelectorAll("input[type='checkbox']:checked");
		
		deleteList.forEach((target) => {
			target.closest("li").remove();
		})
		
		location.reload();
		
	
		// 체크된 목록 삭제하기
		console.log("delete list!");
		console.log(deleteList[0].closest("li"));
	}
	
	let nickName;
	let fileName;
	let imgSrc;
	function activateInput(event) { // 수정버튼 클릭시 input 활성화.
		const targetWrapper = event.target.closest(".nickname-detail-area");
		// 사진첨부 css 수정
		targetWrapper.querySelector(".add-file").classList.remove("disable-input");
		targetWrapper.querySelector(".add-file button").classList.remove("disable-button");
		
		// input 활성화
		targetWrapper.querySelector(".nickname input").disabled = false;
		targetWrapper.querySelector(".nickname input").readOnly = false;
		targetWrapper.querySelector(".profile-image input").disabled = false;
	
		// 버튼 활성화
		targetWrapper.querySelector(".update-button").style.display = "none";
		targetWrapper.querySelector(".save-button").style.display = "block";
		targetWrapper.querySelector(".cancle-button").style.display = "block";
		
		// id정보 버튼에 저장
		const id = document.querySelector(".update-button").dataset.id;
		targetWrapper.querySelector(".save-button").dataset.id = id;
		targetWrapper.querySelector(".cancle-button").dataset.id = id;
		
		// 원래이름 저장
		nickName = targetWrapper.querySelector(".nickname input").value;
		fileName = targetWrapper.querySelector(".add-file span").textContent;
		imgSrc = targetWrapper.querySelector(".thumnail").getAttribute("src");
	}
	
	function cancleUpdate(event) { // 취소버튼 클릭시 input 비활성화.
		const targetWrapper = event.target.closest(".nickname-detail-area");
		// 사진첨부 css 수정
		targetWrapper.querySelector(".add-file").classList.add("disable-input");
		targetWrapper.querySelector(".add-file button").classList.add("disable-button");
		
		// input 비활성화
		targetWrapper.querySelector(".nickname input").disabled = true;
		targetWrapper.querySelector(".nickname input").readOnly = true;
		targetWrapper.querySelector(".profile-image input").disabled = true;
	
		// 버튼 활성화
		targetWrapper.querySelector(".update-button").style.display = "block";
		targetWrapper.querySelector(".save-button").style.display = "none";
		targetWrapper.querySelector(".cancle-button").style.display = "none";
		
		// 원래이름 세팅
		targetWrapper.querySelector(".nickname input").value = nickName;
		targetWrapper.querySelector(".add-file span").textContent = fileName;
		targetWrapper.querySelector(".profile-image input").value = "";
		targetWrapper.querySelector(".thumnail").setAttribute("src", imgSrc);
	}
	
	let file = "";
	function setFileName(event) {
		const imageWrap = event.target.closest(".profile-image");
		const target = imageWrap.querySelector("input[type='file']").files[0];
		
		if(target !== undefined) {
			imageWrap.querySelector(".add-file span").textContent = target.name;
			file = imageWrap.querySelector("input[type='file']").files[0];
			
			const reader = new FileReader();
			reader.onload = (event) => {
				const thumnail = imageWrap.querySelector(".thumnail");
				thumnail.setAttribute("src", event.target.result);
			};
			
			reader.readAsDataURL(event.target.files[0]);
		}
		console.log(file); // 보낼 파일 정보
	}

	function addNickname() {
		alert("추가!");
	}

	function cancleAdd(event) {
		Swal.fire({
			icon: "info",
			text: "작성하신 내용은 저장되지 않습니다",
			showCancelButton: true,
			confirmButtonText: "확인",
			cancelButtonText: "취소"
		}).then((result) => {
			if(result.isConfirmed) {
				const targetWrapper = event.target.closest(".nickname-add-area");
				targetWrapper.querySelector("input[type='file']").value = "";
				targetWrapper.querySelector("input[type='text']").value = "";
				targetWrapper.querySelector(".add-file span").textContent = "파일을 선택하세요";
				targetWrapper.querySelector(".thumnail").setAttribute("src", "/admin/images/profile/photo.svg");
			}
		});
	}
})

