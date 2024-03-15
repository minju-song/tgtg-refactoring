package com.malzzang.tgtg.chatroom.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.malzzang.tgtg.chatroom.service.ChatroomService;
import com.malzzang.tgtg.chatroom.service.ConnectedUserService;
import com.malzzang.tgtg.member.oauth.PrincipalDetails;
import com.malzzang.tgtg.anonymous.dto.AnonymousDTO;
import com.malzzang.tgtg.anonymous.service.AnonymousService;
import com.malzzang.tgtg.chatroom.dto.Chatroom;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class ChatroomController {
	
	@Autowired
	ChatroomService chatroomService;
	
	@Autowired
	ConnectedUserService connectedUserService;
	
	@Autowired
	AnonymousService anonymousService;
	
	@GetMapping("/user/waitChatroom")
	public String startChat(@AuthenticationPrincipal PrincipalDetails principalDetails,@RequestParam String type, HttpServletResponse response, Model model) {
		
	    Chatroom room = chatroomService.findTextRoom();

	    AnonymousDTO anonymous = anonymousService.createAnonymous(room.getRoomId(), principalDetails.getName());
	    
	    //타입은 text/voice
	    room.setType(type);

	    model.addAttribute("room", room);
	    model.addAttribute("anonymous", anonymous);
	    
		return "chat/waitChatroom.html";
	}
	
	@GetMapping("/user/textGame")
	   public String textGame(int roomId, Model model) {
		
	       //Chatroom room = chatroomService.findTextRoom();
	       Chatroom room = new Chatroom(roomId, "text");
	       
	       model.addAttribute("room", room);
	      
	      return "chat/textChatGame.html";
	   }
}
