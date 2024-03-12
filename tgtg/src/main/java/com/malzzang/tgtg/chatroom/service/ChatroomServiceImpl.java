package com.malzzang.tgtg.chatroom.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.malzzang.tgtg.anonymous.dto.AnonymousDTO;
import com.malzzang.tgtg.chatroom.dto.Chatroom;

@Service
public class ChatroomServiceImpl implements ChatroomService{
	
	@Autowired
	ConnectedUserService connectedUserService;

	@Override
	public Chatroom findTextRoom() {
		
		int roomId = 0;
		while(true) {
			int count = connectedUserService.getConnectedUserCount(roomId);
			if(count < 12) break;
			roomId++;
		}
	    
        Chatroom room = Chatroom.builder()
        		.roomId(roomId)
        		.type("text")
        		.build();
        
        return room;
	}

}
