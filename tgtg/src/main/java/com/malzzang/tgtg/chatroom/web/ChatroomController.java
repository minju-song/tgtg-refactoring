package com.malzzang.tgtg.chatroom.web;

import com.malzzang.tgtg.proxyserver.controller.ProxyController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.malzzang.tgtg.chatroom.service.ChatroomService;
import com.malzzang.tgtg.chatroom.service.ConnectedUserService;
import com.malzzang.tgtg.member.oauth.PrincipalDetails;
import com.malzzang.tgtg.anonymous.dto.AnonymousDTO;
import com.malzzang.tgtg.anonymous.service.AnonymousService;
import com.malzzang.tgtg.chatroom.dto.Chatroom;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ChatroomController {
	
	@Autowired
	ChatroomService chatroomService;
	
	@Autowired
	ProxyController proxy;
	
	@GetMapping("/user/waitChatroom")
	public String startChat(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestParam String type, Model model) {

		Map<String, Object> response = new HashMap<>();

		response = (Map<String, Object>) proxy.getChatData(principalDetails.getName(), type);

	    model.addAttribute("room", response.get("room"));
	    model.addAttribute("anonymous", response.get("anonymous"));
	    
		return "chat/waitChatroom.html";
	}
	
	@GetMapping("/user/textGame")
	   public String textGame(int roomId, int anonymousId, Model model) {

		Map<String,Object> room = proxy.getGame(roomId);
	       
		model.addAttribute("room", room);
       	model.addAttribute("anonymousId", anonymousId);

	   	return "chat/textChatGame.html";
	   }
	
	@GetMapping("/user/voiceGame")
	   public String voiceGame(int roomId, int anonymousId, Model model) {

		Map<String,Object> room = proxy.getGame(roomId);

		model.addAttribute("room", room);
		model.addAttribute("anonymousId", anonymousId);
	      
	      return "chat/voiceChatGame.html";
	   }
}
