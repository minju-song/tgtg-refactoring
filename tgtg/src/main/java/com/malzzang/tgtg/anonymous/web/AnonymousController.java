package com.malzzang.tgtg.anonymous.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AnonymousController {
	
	@GetMapping("/admin/anonymousList")
	public String anonymousList() {
		return "admin/anonymousList.html";
	}
}
