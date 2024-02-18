package com.malzzang.tgtg.chatroom.web;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.malzzang.tgtg.chatroom.model.Chat;
import com.malzzang.tgtg.chatroom.model.ChatMessage;

import lombok.RequiredArgsConstructor;

import com.malzzang.tgtg.chatroom.service.ConnectedUserService;
import com.malzzang.tgtg.chatroom.service.ReadyUserService;

@Controller
@RequiredArgsConstructor
public class ChatController {
	
	private final ReadyUserService readyUserService;
	private final ConnectedUserService connectedUserService;

	//메시지 전송 메소드
	@MessageMapping("/{roomId}") //여기로 전송되면 메서드 호출 -> WebSocketConfig prefixes 에서 적용한건 앞에 생략
    @SendTo("/room/{roomId}")   //구독하고 있는 장소로 메시지 전송 (목적지)  -> WebSocketConfig Broker 에서 적용한건 앞에 붙어줘야됨
    public ChatMessage chat(@DestinationVariable int roomId, ChatMessage message) {

        return ChatMessage.builder()
                .roomId(roomId)
                .sender(message.getSender())
                .senderEmail(message.getSenderEmail())
                .message(message.getMessage())
                .build();
	}
	
	//게임 준비 메소드
	@MessageMapping("/{roomId}/ready")
    @SendTo("/room/{roomId}/getReady")
    public int ready(@DestinationVariable int roomId) {
        readyUserService.readyUser(roomId);
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
    @SendTo("/room/{roomId}/connectedCount")
    public int enter(@DestinationVariable int roomId) {
        connectedUserService.userEntered(roomId);
        return connectedUserService.getConnectedUserCount(roomId);
    }

	//회원이 게임 대기방 퇴장했을 때
    @MessageMapping("/{roomId}/leave")
    @SendTo("/room/{roomId}/connectedCount")
    public int leave(@DestinationVariable int roomId) {
        connectedUserService.userLeft(roomId);
        return connectedUserService.getConnectedUserCount(roomId);
    }
}
