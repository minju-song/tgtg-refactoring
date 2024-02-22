package com.malzzang.tgtg.member.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.malzzang.tgtg.member.oauth.PrincipalDetails;

@Controller
public class MemberController {

	@GetMapping("/login")
	public String home() {
		return "member/login.html";
	}
	
	@GetMapping("/user")
	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		System.out.println("principalDetails : " +principalDetails);
		//.getMember()
		return "USER";
	}
	
	@GetMapping("/test/oauth/login")
	public @ResponseBody String testOauthLogin(Authentication authentication,
												@AuthenticationPrincipal OAuth2User oauth) { // DI (의존성 주입)
		System.out.println("/test/oauth/login ================ ");
		OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
		System.out.println("authentication : "+oauth2User.getAttributes());
		System.out.println("oauth2User: "+oauth.getAttributes());
		
		return "OAuth2 세션 확인";
	}
}
