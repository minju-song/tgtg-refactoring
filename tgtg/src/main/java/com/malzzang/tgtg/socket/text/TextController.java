package com.malzzang.tgtg.socket.text;

import com.malzzang.tgtg.chatroom.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
@EnableAsync
public class TextController {

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


    //메시지 전송 메소드
    @MessageMapping("/{roomId}/game") //여기로 전송되면 메서드 호출 -> WebSocketConfig prefixes 에서 적용한건 앞에 생략
    @SendTo("/room/{roomId}/game")   //구독하고 있는 장소로 메시지 전송 (목적지)  -> WebSocketConfig Broker 에서 적용한건 앞에 붙어줘야됨
    public ChatMessage gameChat(@DestinationVariable int roomId, ChatMessage message) {

        ChatMessage msg = ChatMessage.builder()
                .roomId(roomId)
                .sender(message.getSender())
                .senderEmail(message.getSenderEmail())
                .senderImage(message.getSenderImage())
                .message(message.getMessage())
                .build();

        msg.setGameRole(message.getGameRole());
        return msg;
    }
}
