package com.malzzang.tgtg.anonymous.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.malzzang.tgtg.anonymous.Anonymous;
import com.malzzang.tgtg.anonymous.AnonymousRepository;
import com.malzzang.tgtg.anonymous.dto.AnonymousDTO;
import com.malzzang.tgtg.chatroom.service.ConnectedUserService;

@Service
public class AnonymousServiceImpl implements AnonymousService {
	
	@Autowired
	AnonymousRepository anonymousRepository;
	
	//익명아이디 - 실제아이디 맵핑
	private final Map<String, String> anonymousMemberMapping = new HashMap<>();
	
	//익명아이디 가져오기 위해 count
	private final Map<Integer, Integer> anonymousCount = new HashMap<>();

	@Override
	public List<AnonymousDTO> selectAnonymousList() {
		List<Anonymous> anonymousList = anonymousRepository.findAll();
		List<AnonymousDTO> anonymousDtoList = anonymousList.stream()
												.map(Anonymous::toResponseDto)
												.collect(Collectors.toList());
		return anonymousDtoList;
	}

	@Override
	public AnonymousDTO selectAnonymous(int anonymousId) {
		return anonymousRepository.findByAnonymousId(anonymousId).toResponseDto();
	}

	@Override
	public AnonymousDTO getAnonymous(int count) {
		return anonymousRepository.findByAnonymousId(count).toResponseDto();
	}

	//회원의 익명 객체 및 저장
	@Override
	public AnonymousDTO createAnonymous(int roomId, String memberId) {
		int count = anonymousCount.getOrDefault(roomId, 0) + 1;
	    
	    //방번호2자리 + 익명아이디 2자리
	    int anonyId = Integer.parseInt(String.format("%03d%02d", roomId, count));
	    
	    //익명객체 생성
	    AnonymousDTO anonymous = getAnonymous(count);
	    anonymous.setAnonymousId(anonyId);
	    
	    anonymousMemberMapping.put(String.valueOf(anonymous.getAnonymousId()), memberId);
	    anonymousCount.put(roomId, anonymousCount.getOrDefault(roomId, 0)+1);
		return anonymous;
	}

	@Override
	public void deleteAnonymous(int anonymousId) {
		anonymousMemberMapping.remove(anonymousId);
	}

	@Override
	public String findMemberId(String anonymousId) {
		return anonymousMemberMapping.get(anonymousId);
	}

	@Override
	public void deleteCount(int roomId) {
		anonymousCount.remove(roomId);
		
	}
	

}
