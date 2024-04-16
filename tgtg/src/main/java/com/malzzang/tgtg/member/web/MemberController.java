package com.malzzang.tgtg.member.web;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.malzzang.tgtg.member.dto.MemberDTO;
import com.malzzang.tgtg.member.oauth.PrincipalDetails;
import com.malzzang.tgtg.member.service.MemberService;

import groovyjarjarantlr4.v4.parse.ANTLRParser.v3tokenSpec_return;

@Controller
public class MemberController {

	@Autowired
	MemberService memberService;
	

	@GetMapping("/login")
	public String home() {
		return "member/login.html";
	}
	
	/*
	 *  소셜 최초 로그인 시 MBTI 검사 페이지로 이동 
	 */
	@GetMapping("/oauth2/check")
	public String oauth2Check(@AuthenticationPrincipal PrincipalDetails principalDetails, HttpServletResponse response) {

//		여기서 jwt생성하기

		String token = memberService.getJWT(principalDetails.getName());

		// JWT 토큰을 쿠키에 설정
		Cookie cookie = new Cookie("jwtToken", token);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setMaxAge(60 * 60 * 24); // 쿠키 유효 시간을 24시간으로 설정
		response.addCookie(cookie);

		if (principalDetails.getMember().getMemberMbti() == null) {
			return "redirect:/checkMbti";
		}
		return "redirect:/";
	}
	
	/**
	 * MBTI 검사 
	 */
	@GetMapping("/checkMbti")
	public String checkMbti() {
		return "member/checkMbti.html";
	}
	
	/**
	 *  MBTI 업데이트
	 *  @param userMbti
	 *  @param principalDetails
	 * 	@return
	 */
	@PostMapping("/updateMbti")
	@ResponseBody
	public Map<String, Object> updateMbti(@RequestBody Map<String, Object> userMbti,
										  @AuthenticationPrincipal PrincipalDetails principalDetails) {
		//Map<String, Object> result = new HashMap<String, Object>();
		String memberId = principalDetails.getMember().getMemberId();
		String memberRole = principalDetails.getMember().getMemberRole();
		String memberMbti = (String) userMbti.get("mbti");
		
		System.out.println("==="+ memberId +"//"+ memberRole +"//"+ memberMbti);
		return memberService.updateMemberMbti(memberId, memberRole, memberMbti);
	}
	
	@GetMapping("/mypage")
	public String mypage(@AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
		model.addAttribute("myInfo", memberService.selectMemberInfo(principalDetails.getMember().getMemberId()));
				
		return "member/mypage.html";
	}
	
	@PostMapping("/withdrawal")
	@ResponseBody
	public String memberWithdrawal(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		boolean result = memberService.deleteMember(principalDetails.getMember().getMemberId());
		
		if(result) {
			return "success";
		}
		return "fail";
	}
	
	  
	@GetMapping("/test/user")
	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		System.out.println("principalDetails : " +principalDetails.getMember().getMemberMbti());
		System.out.println("principalDetails : " +principalDetails.getMember().getMemberEmail());
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
