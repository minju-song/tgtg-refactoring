package com.malzzang.tgtg.chatroom.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.malzzang.tgtg.chatroom.service.ChatroomService;
import com.malzzang.tgtg.chatroom.service.ConnectedUserService;
import com.malzzang.tgtg.member.oauth.PrincipalDetails;
import com.malzzang.tgtg.anonymous.Anonymous;
import com.malzzang.tgtg.anonymous.dto.AnonymousDTO;
import com.malzzang.tgtg.anonymous.service.AnonymousService;
import com.malzzang.tgtg.chatroom.dto.Chatroom;
import com.malzzang.tgtg.chatroom.dto.GameRequest;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class ChatroomController {
	
	@Autowired
	ChatroomService chatroomService;
	
	@Autowired
	ConnectedUserService connectedUserService;
	
	@Autowired
	AnonymousService anonymousService;
	
	@GetMapping("/user/waitChatroom")
	public String startChat(@AuthenticationPrincipal PrincipalDetails principalDetails,HttpSession session,@RequestParam String type, HttpServletResponse response, Model model) {
		
	    Chatroom room = chatroomService.findTextRoom();

	    AnonymousDTO anonymous = anonymousService.createAnonymous(room.getRoomId(), principalDetails.getName());
	    
	    //타입은 text/voice
	    room.setType(type);

	    session.setAttribute("anonymous", anonymous);

	    model.addAttribute("room", room);
	    model.addAttribute("anonymous", anonymous);
	    
		return "chat/waitChatroom.html";
	}
	
	@GetMapping("/user/textGame")
	   public String textGame(int roomId, Model model) {
	      
		//AnonymousDTO anonymous = (AnonymousDTO) session.getAttribute("anonymous");
			//System.out.println("====>"+anonymous.getAnonymousNickname());
			System.out.println(roomId);
//			System.out.println(roomId);
	       //Chatroom room = chatroomService.findTextRoom();
	       Chatroom room = new Chatroom(roomId, "text");
	       
		   model.addAttribute("room", room);
	       //model.addAttribute("anonymous", anonymous);
	      
	      return "chat/textChatGame.html";
	   }
}
