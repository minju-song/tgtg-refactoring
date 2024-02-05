package com.malzzang.tgtg.member.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberController {

	@GetMapping("/loginPage")
	public String home() {
		return "member/login.html";
	}
}
