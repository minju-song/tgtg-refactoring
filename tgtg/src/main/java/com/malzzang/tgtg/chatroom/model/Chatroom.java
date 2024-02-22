package com.malzzang.tgtg.chatroom.model;

import lombok.Builder;
import lombok.Data;

@Data
public class Chatroom {
	
	private int roomId;
	private String type;
	
	@Builder
    public Chatroom(int roomId, String type) {
		//채팅방 아이디 임의로 지정
        this.roomId = roomId;
        this.type = type;
    }
	
	
}
