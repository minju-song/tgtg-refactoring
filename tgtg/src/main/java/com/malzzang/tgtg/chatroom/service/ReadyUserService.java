package com.malzzang.tgtg.chatroom.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReadyUserService {
	
	private final Map<Integer, Integer> readyUser = new HashMap<>();

	//회원이 준비버튼 클릭했을 때 readyUser 값 1 추가
	public void readyUser(int roomId) {
        readyUser.put(roomId, readyUser.getOrDefault(roomId, 0) + 1);
    }
	
	//회원이 준비버튼 취소했을 때 readyUser 값 1 감소
	public void unreadyUser(int roomId) {
        readyUser.put(roomId, readyUser.getOrDefault(roomId, 0) - 1);
    }

	//현재 준비한 회원 수
    public int getReady(int roomId) {
    	System.out.println("준비한 회원 수 : "+readyUser.getOrDefault(roomId, 0));
        return readyUser.getOrDefault(roomId, 0);
    }
    
    public void deleteReadyUser(int roomId) {
    	readyUser.remove(roomId);
    }

}
