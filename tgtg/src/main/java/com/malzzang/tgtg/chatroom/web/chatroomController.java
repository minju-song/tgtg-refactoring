package com.malzzang.tgtg.chatroom.web;

import java.util.Random;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class chatroomController {
	
	@GetMapping("/user/startChat")
	public String startChat(HttpServletResponse response, Model model) {
		
		// 사용자마다 임의 토큰 생성
	    String userToken = UUID.randomUUID().toString();

	    // 쿠키를 생성하고, 생성한 토큰을 저장
	    Cookie cookie = new Cookie("userToken", userToken);

	    // 쿠키의 유효 시간 1시간
	    cookie.setMaxAge(60 * 60);

	    // 쿠키를 응답에 추가
	    response.addCookie(cookie);

	    // 일단 임의로 1 ~ 3 방 아이디만 지정
	    Random rand = new Random();
        int roomId = rand.nextInt(3) + 1;
	    model.addAttribute("roomId", roomId);
		
		return "chat/waitChatroom.html";
	}
}
