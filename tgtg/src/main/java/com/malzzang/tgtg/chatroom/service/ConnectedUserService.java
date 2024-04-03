package com.malzzang.tgtg.chatroom.service;

import java.util.ArrayList;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.malzzang.tgtg.anonymous.dto.AnonymousDTO;
import com.malzzang.tgtg.anonymous.service.AnonymousService;
import com.malzzang.tgtg.member.service.MemberService;

@Service
public class ConnectedUserService {
	
	@Autowired
	AnonymousService anonymousService;
	
	@Autowired
	MemberService memberService;

	private final Map<Integer, Set<AnonymousDTO>> chatRoomMemberList = new HashMap<>();
	
	private final Map<Integer, Integer> gameStartTime = new HashMap<>();
	
	private final Map<Integer, LocalTime> roomEndTime = new HashMap<>();
	
	private final Map<Integer, Integer> voteResultA = new HashMap<>();
	private final Map<Integer, Integer> voteResultB = new HashMap<>();
	private final Map<Integer, Integer> zeroCount = new HashMap<>();
	
	//회원이 접속했을 때
	public void userEntered(int roomId, AnonymousDTO anonymous) {
		
		Set<AnonymousDTO> memberList = chatRoomMemberList.getOrDefault(roomId, new HashSet<>());
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
			System.out.println("A" + count);
		}
		else {
			int count = voteResultB.getOrDefault(roomId, 0) + 1;
			voteResultB.put(roomId, count);
			System.out.println("B" + count);
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
		System.out.println("answerA : "+answerA);
		System.out.println("answerB : "+answerB);
		if(answerA > answerB) return "answerA";
		else if (answerA < answerB) return "answerB";
		else return "draw";
	}

	//회원이 퇴장했을 때
    public void userLeft(int roomId, AnonymousDTO anonymous) {
        
        Set<AnonymousDTO> memberList = chatRoomMemberList.getOrDefault(roomId, new HashSet<>());
        
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
    	Set<AnonymousDTO> memberList = chatRoomMemberList.getOrDefault(roomId, new HashSet<>());
        return memberList.size();
    }
    
    //특정 방의 모든 회원 가져오기
    public Set<AnonymousDTO> getAllMembersInRoom(int roomId) {
        return chatRoomMemberList.getOrDefault(roomId, new HashSet<>());
    }
    
    //회원 역할
    public Set<AnonymousDTO> setRole(int roomId) {
    	Set<AnonymousDTO> set = chatRoomMemberList.get(roomId);
    	List<AnonymousDTO> list = new ArrayList<>(set); 

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
    		list.get(list.size()-1).setRole("answerB");
    	}
    	
    	// List를 다시 Set으로 변환
        Set<AnonymousDTO> resultSet = new HashSet<>(list);
        chatRoomMemberList.put(roomId, resultSet);
    	
    	return resultSet;
    	
    }
    
    public void saveResult(int roomId, String result) {
    	Set<AnonymousDTO> set = chatRoomMemberList.get(roomId);
    	if(result.equals("answerA")) {
    		for (AnonymousDTO user : set) {
                if(user.getRole().equals("answerA")) {
                	String memberId = anonymousService.findMemberId(Integer.toString( user.getAnonymousId()));
                	System.out.println(memberId);
                	memberService.increaseWin(memberId);                  
                }
                else if(user.getRole().equals("answerB")) {
                	String memberId = anonymousService.findMemberId(Integer.toString( user.getAnonymousId()));
                	System.out.println(memberId);
                	memberService.increaseLose(memberId);  
                }
                anonymousService.deleteAnonymous(user.getAnonymousId());
            }
    	}
    	else if (result.equals("answerB")) {
    		for (AnonymousDTO user : set) {
                if(user.getRole().equals("answerB")) {
                	String memberId = anonymousService.findMemberId(Integer.toString( user.getAnonymousId()));
                	System.out.println(memberId);
                	memberService.increaseWin(memberId);                  
                }
                else if(user.getRole().equals("answerA")) {
                	String memberId = anonymousService.findMemberId(Integer.toString(user.getAnonymousId()));
                	System.out.println(memberId);
                	memberService.increaseLose(memberId);  
                }
                anonymousService.deleteAnonymous(user.getAnonymousId());
            }
    	}
    	else {
    		for (AnonymousDTO user : set) {
                if(user.getRole().equals("answerA") || user.getRole().equals("answerB")) {
                	String memberId = anonymousService.findMemberId(Integer.toString( user.getAnonymousId()));
                	System.out.println(memberId);
                	memberService.increaseDraw(memberId);
                }
                anonymousService.deleteAnonymous(user.getAnonymousId());
            }
    	}
    	deleteRoom(roomId);
    }
    
    public void deleteRoom(int roomId) {
    	chatRoomMemberList.remove(roomId);
    	gameStartTime.remove(roomId);
    	roomEndTime.remove(roomId);
    	voteResultA.remove(roomId);
    	voteResultB.remove(roomId);
    	zeroCount.remove(roomId);
    }
}
