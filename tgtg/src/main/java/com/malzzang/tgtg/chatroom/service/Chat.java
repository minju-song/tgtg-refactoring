package com.malzzang.tgtg.chatroom.service;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Chat {
	
	private Chatroom room;
	private String sender;
    private String senderEmail;
    private String message;
    private LocalDateTime sendDate;
    
    @Builder
    public Chat(Chatroom room, String sender, String senderEmail, String message) {
        this.room = room;
        this.sender = sender;
        this.senderEmail = senderEmail;
        this.message = message;
        this.sendDate = LocalDateTime.now();
    }
    
    public static Chat createChat(Chatroom room, String sender, String senderEmail, String message) {
        return Chat.builder()
                .room(room)
                .sender(sender)
                .senderEmail(senderEmail)
                .message(message)
                .build();
    }

	
}
