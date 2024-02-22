package com.malzzang.tgtg.chatroom.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ConnectedUserService {

	private final Map<Integer, Integer> connectedUserCount = new HashMap<>();
	
	//회원이 접속했을 때
	public void userEntered(int roomId) {
        connectedUserCount.put(roomId, connectedUserCount.getOrDefault(roomId, 0) + 1);
    }

	//회원이 퇴장했을 때
    public void userLeft(int roomId) {
        connectedUserCount.put(roomId, connectedUserCount.getOrDefault(roomId, 0) - 1);
    }

    //현재 접속한 회원 수
    public int getConnectedUserCount(int roomId) {
        return connectedUserCount.getOrDefault(roomId, 0);
    }
}
