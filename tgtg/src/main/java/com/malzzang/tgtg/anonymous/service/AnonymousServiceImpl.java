package com.malzzang.tgtg.anonymous.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.malzzang.tgtg.anonymous.Anonymous;
import com.malzzang.tgtg.anonymous.AnonymousRepository;
import com.malzzang.tgtg.anonymous.dto.AnonymousDTO;

@Service
public class AnonymousServiceImpl implements AnonymousService {
	
	@Autowired
	AnonymousRepository anonymousRepository;

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

}
