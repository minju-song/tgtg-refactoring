package com.malzzang.tgtg.chatroom.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.malzzang.tgtg.anonymous.dto.AnonymousDTO;
import com.malzzang.tgtg.chatroom.dto.Chatroom;
import com.malzzang.tgtg.subject.SubjectRepository;
import com.malzzang.tgtg.subject.dto.SubjectDTO;
import com.malzzang.tgtg.subject.Subject;

@Service
public class ChatroomServiceImpl implements ChatroomService{
	
	private final List<Chatroom> rooms = new ArrayList<>();
	
	@Autowired
	ConnectedUserService connectedUserService;
	
	@Autowired
	SubjectRepository subjectRepository;

	@Override
	public Chatroom findTextRoom() {
		
		return findOrCreateRoom("text");
	}

	@Override
	public Chatroom findVoiceRoom() {
		return findOrCreateRoom("voice");
	}
	
	@Override
	public Chatroom findOrCreateRoom(String type) {
		int baseRoomId = type.equals("text") ? 0 : 100;
	    TreeSet<Integer> usedIds = new TreeSet<>(); // 사용 중인 방 번호를 저장

	    // 기존 방 중에서 조건에 맞는 방이 있는지 확인하고, 사용 중인 roomId 기록
	    for (Chatroom room : rooms) {
	        if (room.getType().equals(type)) {
	            usedIds.add(room.getRoomId());
	            if (room.getStatus().equals("ready") && connectedUserService.getConnectedUserCount(room.getRoomId()) < 12) {
	                return room;
	            }
	        }
	    }

	    // 가능한 가장 낮은 roomId 찾기
	    int newRoomId = baseRoomId;
	    for (Integer id : usedIds) {
	        if (id == newRoomId) {
	            newRoomId++; // 현재 ID가 사용 중이면, 다음 번호로
	        } else {
	            break; // 사용 중이지 않은 첫 번호에서 반복문 탈출
	        }
	    }

	    // 새 방 생성
	    Chatroom newRoom = Chatroom.builder()
	            .roomId(newRoomId)
	            .type(type)
	            .status("ready")
	            .build();
	    rooms.add(newRoom);
	    return newRoom;
    }
	
    // roomId를 토대로 Chatroom 객체를 반환하는 함수
	@Override
    public Chatroom getRoomById(int roomId) {
        for (Chatroom room : rooms) {
            if (room.getRoomId() == roomId) {
                return room;
            }
        }
        return null; // 찾지 못한 경우 null 반환
    }

	@Override
	public void setRoomStatusToRun(int roomId) {
		for (Chatroom room : rooms) {
	        if (room.getRoomId() == roomId) {
	            room.setStatus("run"); // 상태를 "run"으로 변경
	            return;
	        }
	    }
		
		System.out.println("해당 ID에 해당하는 방이 없습니다.");
		
	}

	@Override
	public boolean removeRoomById(int roomId) {
		Iterator<Chatroom> iterator = rooms.iterator();
	    
	    while (iterator.hasNext()) {
	        Chatroom room = iterator.next();
	        if (room.getRoomId() == roomId) {
	            iterator.remove(); // 해당 roomId를 가진 방을 리스트에서 제거
	            return true; // 성공적으로 삭제되었음을 나타내는 true 반환
	        }
	    }
	    
	    // roomId에 해당하는 방을 찾지 못했을 경우
	    return false; // 삭제에 실패했음을 나타내는 false 반환
	}

	@Override
	public void setTitle(int roomId) {
		int len = subjectRepository.findAll().size();
		
		int num = (int) (Math.floor(Math.random() * len) + 1);
		
		Optional<Subject> subject = subjectRepository.findById(num);
		for (Chatroom room : rooms) {
            if (room.getRoomId() == roomId) {
                room.setTitle(subject.get().getSubjectTitle());
                room.setAnswerA(subject.get().getSubjectAnswerA());
                room.setAnswerB(subject.get().getSubjectAnswerB());
            }
        }
		
		
	}

	@Override
	public Set<AnonymousDTO> startGame(int roomId) {
		// TODO Auto-generated method stub
		setRoomStatusToRun(roomId);
    	setTitle(roomId);
    	Set<AnonymousDTO> memberSet = connectedUserService.setRole(roomId);
    	
    	return memberSet;
	}



}
