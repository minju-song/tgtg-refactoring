package com.malzzang.tgtg.chatroom.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ReadyUserService {
	
	private final Map<Integer, Boolean> readyUser = new HashMap<>();

	//회원이 준비버튼 클릭했을 때
	public void readyUser(int roomId) {
        readyUser.put(roomId, true);
    }
	
	//회원이 준비버튼 취소했을 때
	public void unreadyUser(int roomId) {
        readyUser.put(roomId, false);
    }

    public boolean getReady(int roomId) {
        return readyUser.getOrDefault(roomId, false);
    }

}
