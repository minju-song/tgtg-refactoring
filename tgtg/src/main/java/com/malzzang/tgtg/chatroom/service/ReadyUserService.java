package com.malzzang.tgtg.chatroom.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReadyUserService {
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	private final Map<Integer, Integer> readyUser = new HashMap<>();

	//회원이 준비버튼 클릭했을 때 readyUser 값 1 추가
	public void readyUser(int roomId) {
		String key = "chatRoom:" + roomId + ":readyUser";
		redisTemplate.opsForValue().increment(key);
    }
	
	//회원이 준비버튼 취소했을 때 readyUser 값 1 감소
	public void unreadyUser(int roomId) {
		String key = "chatRoom:" + roomId + ":readyUser";
		redisTemplate.opsForValue().decrement(key);
    }

	//현재 준비한 회원 수
    public int getReady(int roomId) {
    	String key = "chatRoom:" + roomId + ":readyUser";
    	String value = redisTemplate.opsForValue().get(key);
    	return value != null ? Integer.parseInt(value) : 0;
    }
    
    public void deleteReadyUser(int roomId) {
    	String key = "chatRoom:" + roomId + ":readyUser";
    	redisTemplate.delete(key);
    }

}
