package com.malzzang.tgtg.chatroom.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

import com.malzzang.tgtg.chatroom.service.ConnectedUserService;
import com.malzzang.tgtg.chatroom.service.ReadyUserService;
import com.malzzang.tgtg.anonymous.Anonymous;
import com.malzzang.tgtg.anonymous.dto.AnonymousDTO;
import com.malzzang.tgtg.anonymous.service.AnonymousService;
import com.malzzang.tgtg.chatroom.dto.Chat;
import com.malzzang.tgtg.chatroom.dto.ChatMessage;

@Controller
@RequiredArgsConstructor
public class ChatController {
	
	private final ReadyUserService readyUserService;
	private final ConnectedUserService connectedUserService;
	private final AnonymousService anonymousService;
	
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	//메시지 전송 메소드
	@MessageMapping("/{roomId}") //여기로 전송되면 메서드 호출 -> WebSocketConfig prefixes 에서 적용한건 앞에 생략
    @SendTo("/room/{roomId}")   //구독하고 있는 장소로 메시지 전송 (목적지)  -> WebSocketConfig Broker 에서 적용한건 앞에 붙어줘야됨
    public ChatMessage chat(@DestinationVariable int roomId, ChatMessage message) {

        return ChatMessage.builder()
                .roomId(roomId)
                .sender(message.getSender())
                .senderEmail(message.getSenderEmail())
                .senderImage(message.getSenderImage())
                .message(message.getMessage())
                .build();
	} 
	
	//게임 준비 메소드
	@MessageMapping("/{roomId}/ready")
    @SendTo("/room/{roomId}/getReady")
    public int ready(@DestinationVariable int roomId) {
        readyUserService.readyUser(roomId);
        if(readyUserService.getReady(roomId) == connectedUserService.getConnectedUserCount(roomId) && 
        		readyUserService.getReady(roomId)>= 3	) {
        	System.out.println("스타트");
        	simpMessagingTemplate.convertAndSend("/room/" + roomId + "/startGame", "Start");
        }
        return readyUserService.getReady(roomId);
    }
	
	//게임 준비취소 메소드
	@MessageMapping("/{roomId}/unready")
    @SendTo("/room/{roomId}/getReady")
    public int unready(@DestinationVariable int roomId) {
        readyUserService.unreadyUser(roomId);
        return readyUserService.getReady(roomId);
    }
	
	//현재 준비한 회원 수
	@MessageMapping("/{roomId}/getReadyCount")
	@SendTo("/room/{roomId}/getReady")
	public int getReady(@DestinationVariable int roomId) {
	    return readyUserService.getReady(roomId);
	}
	
	//회원이 게임 대기방 들어왔을 때
	@MessageMapping("/{roomId}/enter")
    @SendTo("/room/{roomId}/connect")
    public Map enter(@DestinationVariable int roomId, AnonymousDTO anonymous) {
		
		//인원수 추가해줌
        connectedUserService.userEntered(roomId, anonymous);
        
        //해당방 회원
        List<AnonymousDTO> list = connectedUserService.getAllMembersInRoom(roomId);
        
        //리턴값 담을 맵
        Map<String, Object> map = new HashMap<>();
        map.put("connectUser", connectedUserService.getConnectedUserCount(roomId));
        map.put("anonymous", anonymous);
        map.put("enter", true);
        map.put("memberList", list);
        
        return map;
    }

	//회원이 게임 대기방 퇴장했을 때
    @MessageMapping("/{roomId}/leave")
    @SendTo("/room/{roomId}/connect")
    public Map leave(@DestinationVariable int roomId, AnonymousDTO anonymous) {

        connectedUserService.userLeft(roomId, anonymous);
        anonymousService.deleteAnonymous(anonymous.getAnonymousId());
        
        //해당방 회원
        List<AnonymousDTO> list = connectedUserService.getAllMembersInRoom(roomId);
        
        //리턴값 담을 맵
        Map<String, Object> map = new HashMap<>();
        map.put("connectUser", connectedUserService.getConnectedUserCount(roomId));
        map.put("anonymous", anonymous);
        map.put("enter", false);
        map.put("memberList", list);
        
        return map;
    }
    
    //게임방 메시지 전송 메소드
  	@MessageMapping("/{roomId}/game")
	@SendTo("/room/{roomId}/game")
	public ChatMessage gameChat(@DestinationVariable int roomId, ChatMessage message) {
	    ChatMessage messages = ChatMessage.builder()
	            .roomId(roomId)
	            .sender(message.getSender())
	            .senderEmail(message.getSenderEmail())
	            .message(message.getMessage())
	            .build();
	    
	    
  		return messages;
  	}
}
