package com.malzzang.tgtg.chatroom.dto;

import com.malzzang.tgtg.anonymous.dto.AnonymousDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameRequest {
	private AnonymousDTO anonymous;
	private Chatroom room;
}
