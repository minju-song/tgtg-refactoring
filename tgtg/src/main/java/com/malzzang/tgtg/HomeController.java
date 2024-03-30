package com.malzzang.tgtg;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.malzzang.tgtg.mail.MailService;
import com.malzzang.tgtg.member.service.MemberService;


@Controller
public class HomeController {
	
	@Autowired
	MemberService memberService;
	
	@Autowired
	MailService mailService;
	
	@Value("${admin.mail}")
	private String mail;
	
	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("memberWin", memberService.selectMemberWin());
		System.out.println(memberService.selectMemberWin());
		return "index.html";
	}
	
	@PostMapping("/contactEmail")
	@ResponseBody
	public String contactEmail(@RequestBody Map<String, Object> contact) throws Exception {
		System.out.println(contact);
		
		Boolean result = mailService.sendSimpleMessage(mail, (String)contact.get("emailEmail"), (String)contact.get("emailName"), (String)contact.get("emailContent"));
		
		if(result) {
			return "success";
		}
		return "fail";
	}
}
