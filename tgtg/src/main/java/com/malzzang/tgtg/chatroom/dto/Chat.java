package com.malzzang.tgtg.chatroom.dto;

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
    private String senderImage;
    private LocalDateTime sendDate;
    
    @Builder
    public Chat(Chatroom room, String sender, String senderEmail,String senderImage, String message) {
        this.room = room;
        this.sender = sender;
        this.senderEmail = senderEmail;
        this.senderImage = senderImage;
        this.message = message;
        this.sendDate = LocalDateTime.now();
    }
    
    public static Chat createChat(Chatroom room, String sender, String senderEmail, String senderImage, String message) {
        return Chat.builder()
                .room(room)
                .sender(sender)
                .senderEmail(senderEmail)
                .senderImage (senderImage)
                .message(message)
                .build();
    }

	
}
