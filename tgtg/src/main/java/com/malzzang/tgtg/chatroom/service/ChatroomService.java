package com.malzzang.tgtg.chatroom.service;

import java.util.ArrayList;
import java.util.List;

import com.malzzang.tgtg.anonymous.dto.AnonymousDTO;
import com.malzzang.tgtg.chatroom.dto.Chatroom;

public interface ChatroomService {
	
	//텍스트방 찾기
	public Chatroom findTextRoom();
	
	//음성방 찾기
	public Chatroom findVoiceRoom();
	
	//방 생성 또는 찾기
	public Chatroom findOrCreateRoom(String type);
	
	//방아이디로 방 객체 받기
	public Chatroom getRoomById(int roomId);
	
	//상태 바꾸기
	public void setRoomStatusToRun(int roomId);
	
	//해당 방 삭제
	public boolean removeRoomById(int roomId);
	
	//해당 방 주제 정하기
	public void setTitle(int roomId);
	
	//게임 시작
	public List<AnonymousDTO> startGame(int roomId);
}
