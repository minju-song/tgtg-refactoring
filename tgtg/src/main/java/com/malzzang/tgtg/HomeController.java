package com.malzzang.tgtg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.malzzang.tgtg.member.service.MemberService;


@Controller
public class HomeController {
	
	@Autowired
	MemberService memberService;
	
	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("memberWin", memberService.selectMemberWin());
		System.out.println(memberService.selectMemberWin());
		return "index.html";
	}
}
