package com.malzzang.tgtg.member.web;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.malzzang.tgtg.member.dto.MemberDTO;
import com.malzzang.tgtg.member.service.MemberService;

@Controller
public class MemberController {

	@Autowired
	MemberService memberService;
	
	@GetMapping("/loginPage")
	public String home() {
		return "member/login.html";
	}
	
	@GetMapping("/admin/member/list")
	public String adminMemberList(Model model, String memberId, String memberStop,
			@PageableDefault(page = 1, size = 10, sort = "memberId", direction = Direction.DESC) Pageable pageable) {
		
		Page<MemberDTO> memberDtoList = null;
		if(memberStop != null && !memberStop.isEmpty()) {
			memberDtoList = memberService.searchMemberStopList(pageable, memberStop);
		} else if(memberId != null && !memberId.isEmpty()) {
			memberDtoList = memberService.searchMemberIdList(pageable, memberId);
		} else {
			memberDtoList = memberService.selectMemberList(pageable);		
		}
		
		Map<String, Object> pagingInfo = memberService.getPagingInfo(pageable, memberDtoList); // 페이징 정보
		
		model.addAttribute("memberList", memberDtoList);
		model.addAttribute("nowPage", memberDtoList.getNumber() + 1);
		model.addAttribute("startPage", pagingInfo.get("startPage"));
		model.addAttribute("endPage", pagingInfo.get("endPage"));
		model.addAttribute("memberId", memberId);
		model.addAttribute("memberStop", memberStop);
		
		return "admin/memberList.html";
	}
	
	@PostMapping("/admin/member/update/memberStop")
	@ResponseBody
	public int adminUpdateMemberStop(@RequestBody Map<String, String> requestData) {
		int result = 0;
		Timestamp memberStop = null;
		String stopYn = requestData.get("stopYn");
		String memberId = requestData.get("memberId");
		
		if(stopYn.equals("stop")) {
			memberStop = Timestamp.valueOf(LocalDateTime.now());
		}
		
		result = memberService.updateMemberStop(memberStop, memberId);
		
		return result;
	}
}
