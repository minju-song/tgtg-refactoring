package com.malzzang.tgtg.common;

import java.util.HashSet;
import java.util.Set;

import com.malzzang.tgtg.anonymous.dto.AnonymousDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameRoleDTO {
	private String url;
	private Set<AnonymousDTO> roleList = new HashSet<>();

}
