package com.malzzang.tgtg.chatroom.service;

import java.util.ArrayList;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.malzzang.tgtg.anonymous.dto.AnonymousDTO;

@Service
public class ConnectedUserService {

	private final Map<Integer, List<AnonymousDTO>> chatRoomMemberList = new HashMap<>();
	
	private final Map<Integer, Integer> gameStartTime = new HashMap<>();
	
	private final Map<Integer, LocalTime> roomEndTime = new HashMap<>();
	
	private final Map<Integer, Integer> voteResultA = new HashMap<>();
	private final Map<Integer, Integer> voteResultB = new HashMap<>();
	private final Map<Integer, Integer> zeroCount = new HashMap<>();
	
	//회원이 접속했을 때
	public void userEntered(int roomId, AnonymousDTO anonymous) {
		
		List<AnonymousDTO> memberList = chatRoomMemberList.getOrDefault(roomId, new ArrayList<>());
	    memberList.add(anonymous);
	    chatRoomMemberList.put(roomId, memberList);
    }
	
	//현재 시간 보냄
	public LocalTime gameStartUser(int roomId) {
		int count = gameStartTime.getOrDefault(roomId, 0) + 1;
		gameStartTime.put(roomId, count);
		if (count >= chatRoomMemberList.get(roomId).size()) {
			LocalTime now = LocalTime.now();
			LocalTime end = roomEndTime.getOrDefault(roomId, now.plusMinutes(1));
			roomEndTime.put(roomId, end);
			return end;
		}
		
		return null;
	}
	
	//게임투표 count
	public void gameVoteCount(int roomId, int gameSelect) {
		if(gameSelect == 0) {
			int count = voteResultA.getOrDefault(roomId, 0) + 1;
			voteResultA.put(roomId, count);
		}
		else {
			int count = voteResultB.getOrDefault(roomId, 0) + 1;
			voteResultB.put(roomId, count);
		}
		
	}
	
	//시간끝난 인원수 체크
	public boolean getZeroCount(int roomId) {
		int count = zeroCount.getOrDefault(roomId, 0) + 1;
		zeroCount.put(roomId, count);
		if(count==chatRoomMemberList.size()) {
			return true;
		}
		else return false;
	}
	
	//투표 결과 리턴
	public String getVoteResult(int roomId) {
		int answerA = voteResultA.getOrDefault(roomId, 0);
		int answerB = voteResultB.getOrDefault(roomId, 0);
		if(answerA > answerB) return "answerA";
		else if (answerA < answerB) return "answerB";
		else return "draw";
	}

	//회원이 퇴장했을 때
    public void userLeft(int roomId, AnonymousDTO anonymous) {
        
        List<AnonymousDTO> memberList = chatRoomMemberList.getOrDefault(roomId, new ArrayList<>());
        
        Iterator<AnonymousDTO> iterator = memberList.iterator();
        while (iterator.hasNext()) {
            AnonymousDTO member = iterator.next();
            if (member.getAnonymousId()==anonymous.getAnonymousId()) {
                iterator.remove();
                break;
            }
        }
        
        chatRoomMemberList.put(roomId, memberList);
    }

    //현재 접속한 회원 수
    public int getConnectedUserCount(int roomId) {
    	List<AnonymousDTO> memberList = chatRoomMemberList.getOrDefault(roomId, new ArrayList<>());
        return memberList.size();
    }
    
    //특정 방의 모든 회원 가져오기
    public List<AnonymousDTO> getAllMembersInRoom(int roomId) {
        return chatRoomMemberList.getOrDefault(roomId, new ArrayList<>());
    }
    
    //회원 역할
    public List<AnonymousDTO> setRole(int roomId) {
    	System.out.println("멤버리스트>>>>>"+chatRoomMemberList.get(roomId));
    	List<AnonymousDTO> list = chatRoomMemberList.get(roomId);
    	for(int i = 0; i < list.size(); i++) {
    		System.out.println(">>>>>>"+list.get(i).getAnonymousNickname()+"   "+list.get(i).getRole());
    	}
    	//3명일때
    	
    	if(list.size() % 3 == 0) {
    		for(int i = 0; i < list.size()/3; i++) {
    			list.get(i*3).setRole("judge");
    			list.get((i*3)+1).setRole("answerA");
    			list.get((i*3)+2).setRole("answerB");
    		}
    	}
    	else if(list.size() % 3 == 1) {
    		for(int i = 0; i < list.size()/3; i++) {
    			list.get(i*3).setRole("judge");
    			list.get((i*3)+1).setRole("answerA");
    			list.get((i*3)+2).setRole("answerB");
    		}
    		list.get(list.size()-1).setRole("judge");
    	}
    	else {
    		for(int i = 0; i < list.size()/3; i++) {
    			list.get(i*3).setRole("judge");
    			list.get((i*3)+1).setRole("answerA");
    			list.get((i*3)+2).setRole("answerB");
    		}
    		list.get(list.size()-2).setRole("answerA");
    		list.get(list.size()-2).setRole("answerB");
    	}
    	
    	chatRoomMemberList.put(roomId, list);
    	
    	return chatRoomMemberList.get(roomId);
    	
    }
}
