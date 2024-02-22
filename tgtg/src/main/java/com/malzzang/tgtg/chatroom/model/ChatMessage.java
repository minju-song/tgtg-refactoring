package com.malzzang.tgtg.chatroom.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {
	private int roomId;
	private String sender;
	private String gameRole;
    private String senderEmail;
    private String message;
    private LocalDateTime sendDate;
    
    @Builder
    public ChatMessage(int roomId, String sender, String senderEmail, String message) {
        this.roomId = roomId;
        this.sender = sender;
        this.senderEmail = senderEmail;
        this.message = message;
    }
    

}
