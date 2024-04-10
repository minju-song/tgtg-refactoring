package com.malzzang.tgtg.anonymous.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.malzzang.tgtg.anonymous.dto.AnonymousDTO;
import com.malzzang.tgtg.anonymous.service.AnonymousService;

@Controller
public class AnonymousController {
	
	@Autowired
	AnonymousService anonymousService;
	
//	/**
//	 * 관리자 익명 목록
//	 * @param model
//	 * @return
//	 */
//	@GetMapping("/management/anonymousList")
//	public String anonymousList(Model model) {
//		model.addAttribute("anonymousList", anonymousService.selectAnonymousList());
//		return "admin/anonymousList.html";
//	}
	
//	/**
//	 * 관리자 익명 단건 정보 조회
//	 * @param anonymousId
//	 * @return
//	 */
//	@GetMapping("/management/anonymousInfo")
//	@ResponseBody
//	public AnonymousDTO anonymousInfo(int anonymousId) {
//		return anonymousService.selectAnonymous(anonymousId);
//	}
	
}
