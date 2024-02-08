package com.malzzang.tgtg.chatroom.service;

import lombok.Builder;
import lombok.Data;

@Data
public class Chatroom {
	
	private int roomId;
	
	@Builder
    public Chatroom() {
		//채팅방 아이디 임의로 지정
        this.roomId = (int)((Math.random()*10000)%10);
    }
	
	
}
