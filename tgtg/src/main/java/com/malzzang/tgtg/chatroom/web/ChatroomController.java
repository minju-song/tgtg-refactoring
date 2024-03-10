package com.malzzang.tgtg.chatroom.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.malzzang.tgtg.chatroom.service.ChatroomService;
import com.malzzang.tgtg.chatroom.service.ConnectedUserService;
import com.malzzang.tgtg.chatroom.dto.Chatroom;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ChatroomController {

	@Autowired
	ChatroomService chatroomService;

	@Autowired
	ConnectedUserService connectedUserService;

	@GetMapping("/user/waitChatroom")
	public String startChat(@RequestParam String type, HttpServletResponse response, Model model) {

		// 사용자마다 임의 토큰 생성
		String userToken = UUID.randomUUID().toString();

		// 쿠키를 생성하고, 생성한 토큰을 저장
		Cookie cookie = new Cookie("userToken", userToken);

		// 쿠키의 유효 시간 1시간
		cookie.setMaxAge(60 * 60);

		// 쿠키를 응답에 추가
		response.addCookie(cookie);

		Chatroom room = chatroomService.findTextRoom();

		int count = connectedUserService.getConnectedUserCount(room.getRoomId());
		Map<String, String> anonymous = new HashMap<>();
		String name = "목청좋은 카다리아" + (count + 1);
		anonymous.put("anonymousNickname", name);
		anonymous.put("anonymousImage", "/admin/images/profile/user-1.jpg");

		// 타입은 text/voice
		room.setType(type);

		model.addAttribute("room", room);
		model.addAttribute("anonymous", anonymous);

		return "chat/waitChatroom.html";
	}

	@GetMapping("/user/textGame")
	public String textGame(HttpServletResponse response, Model model, String anonymousNickname, String anonymousImage) {
		// 사용자마다 임의 토큰 생성
		String userToken = UUID.randomUUID().toString();

		// 쿠키를 생성하고, 생성한 토큰을 저장
		Cookie cookie = new Cookie("userToken", userToken);

		// 쿠키의 유효 시간 1시간
		cookie.setMaxAge(60 * 60);

		// 쿠키를 응답에 추가
		response.addCookie(cookie);

		// Chatroom room = chatroomService.findTextRoom();
		Chatroom room = new Chatroom(3, "text");

		Map<String, String> anonymous = new HashMap<>();
		anonymous.put("anonymousNickname", anonymousNickname);
		anonymous.put("anonymousImage", anonymousImage);
		System.out.println(anonymousNickname);
		model.addAttribute("room", room);
		model.addAttribute("anonymous", anonymous);

		return "chat/textChatGame.html";
	}
}
