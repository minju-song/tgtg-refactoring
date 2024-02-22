package com.malzzang.tgtg.chatroom.service;

import java.util.Random;

import org.springframework.stereotype.Service;

import com.malzzang.tgtg.chatroom.model.Chatroom;

@Service
public class ChatroomServiceImpl implements ChatroomService{

	@Override
	public Chatroom findTextRoom() {
		
		//방 찾거나 개설하는 알고리즘 짜기
		// 일단 임의로 1 ~ 3 방 아이디만 지정
	    Random rand = new Random();
        int roomId = rand.nextInt(3) + 1;
	    
        Chatroom room = Chatroom.builder()
        		.roomId(roomId)
        		.type("text")
        		.build();
        
        return room;
	}

}
