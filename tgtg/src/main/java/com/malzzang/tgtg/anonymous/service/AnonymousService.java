package com.malzzang.tgtg.anonymous.service;

import java.util.List;

import com.malzzang.tgtg.anonymous.dto.AnonymousDTO;

public interface AnonymousService {
	
	public List<AnonymousDTO> selectAnonymousList();
	public AnonymousDTO selectAnonymous(int anonymousId);

}
