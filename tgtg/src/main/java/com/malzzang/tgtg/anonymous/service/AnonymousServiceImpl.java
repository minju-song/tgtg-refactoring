package com.malzzang.tgtg.anonymous.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private StringRedisTemplate redisTemplate;

	// 직렬화
	@Override
	public String serializeAnonymousDTO(AnonymousDTO anonymousDTO) {
	    try {
	        return objectMapper.writeValueAsString(anonymousDTO);
	    } catch (JsonProcessingException e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	// 역직렬화
	@Override
	public AnonymousDTO deserializeToAnonymousDTO(String json) {
	    try {
	        return objectMapper.readValue(json, AnonymousDTO.class);
	    } catch (JsonProcessingException e) {
	        e.printStackTrace();
	        return null;
	    }
	}

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
		String key = "chatRoom:"+roomId+":anonymousCount";
		
		int count = redisTemplate.opsForValue().increment(key).intValue();
		
		int anonyId = Integer.parseInt(String.format("%03d%02d", roomId, count));
		
	    
	    //익명객체 생성
	    AnonymousDTO anonymous = getAnonymous(count);
	    anonymous.setAnonymousId(anonyId);
	    anonymous.setRoomId(roomId);
	    
	    String mappingKey = "chatRoom:"+roomId+":anonymousMemberMapping";
	    redisTemplate.opsForHash().put(mappingKey, Integer.toString(anonyId), memberId);
	    
		return anonymous;
	}

	@Override
	public void deleteAnonymous(int roomId, int anonymousId) {
		String mappingKey = "chatRoom:"+roomId+":anonymousMemberMapping";
	    
	    // 익명 ID를 사용해 해당 익명 사용자의 매핑을 삭제
	    redisTemplate.opsForHash().delete(mappingKey, Integer.toString(anonymousId));
	
	}

	@Override
	public String findMemberId(int roomId, String anonymousId) {
		String mappingKey = "chatRoom:"+roomId+":anonymousMemberMapping";
	    
	    // 익명 ID에 해당하는 회원 ID를 찾아 반환
	    return (String) redisTemplate.opsForHash().get(mappingKey, anonymousId);

	}

	@Override
	public void deleteCount(int roomId) {
		String key = "chatRoom:"+roomId+":anonymousCount";
		
		redisTemplate.delete(key);
		
	}
	

}
