package com.malzzang.tgtg.common;

import java.util.ArrayList;
import java.util.List;

import com.malzzang.tgtg.anonymous.dto.AnonymousDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameRoleDTO {
	private String url;
	private List<AnonymousDTO> roleList = new ArrayList<>();

}
