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
		
		Chatroom room = new Chatroom(0, type, "ready");
		
		if(type.equals("text")) {			
			room = chatroomService.findTextRoom();
		}
		else {
			room = chatroomService.findVoiceRoom();
		}

	    AnonymousDTO anonymous = anonymousService.createAnonymous(room.getRoomId(), principalDetails.getName());
	    
	    //타입은 text/voice
	    room.setType(type);

	    model.addAttribute("room", room);
	    model.addAttribute("anonymous", anonymous);
	    
		return "chat/waitChatroom.html";
	}
	
	@GetMapping("/user/textGame")
	   public String textGame(int roomId,int anonymousId,Model model) {


	       //Chatroom room = chatroomService.findTextRoom();
	       Chatroom room = chatroomService.getRoomById(roomId);
	       
	     model.addAttribute("room", room);
       model.addAttribute("anonymousId", anonymousId);
	      
	      return "chat/textChatGame.html";
	   }
	
	@GetMapping("/user/voiceGame")
	   public String voiceGame(int roomId, int anonymousId, Model model) {

		   Chatroom room = chatroomService.getRoomById(roomId);
	       
	       model.addAttribute("room", room);
	       model.addAttribute("anonymousId", anonymousId);
	      
	      return "chat/voiceChatGame.html";
	   }
}
