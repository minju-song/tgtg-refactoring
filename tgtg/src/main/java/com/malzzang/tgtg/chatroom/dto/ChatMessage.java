package com.malzzang.tgtg.chatroom.dto;

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
    private String senderImage;
    private String message;
    private LocalDateTime sendDate;
    private Integer count;
    
    @Builder
    public ChatMessage(int roomId, String sender, String senderEmail,String senderImage, String message) {
        this.roomId = roomId;
        this.sender = sender;
        this.senderEmail = senderEmail;
        this.senderImage = senderImage;
        this.message = message;
    }
    

}
