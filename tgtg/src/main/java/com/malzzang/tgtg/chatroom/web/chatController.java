package com.malzzang.tgtg.chatroom.web;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.malzzang.tgtg.chatroom.service.Chat;
import com.malzzang.tgtg.chatroom.service.ChatMessage;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class chatController {

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
}
