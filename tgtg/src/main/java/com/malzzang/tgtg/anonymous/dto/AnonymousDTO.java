package com.malzzang.tgtg.anonymous.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AnonymousDTO {
	@JsonProperty("anonymousId")
	private int anonymousId;
	@JsonProperty("anonymousImage")
	private String anonymousImage;
	@JsonProperty("anonymousNickname")
	private String anonymousNickname;
	@JsonProperty("anonymousImageName")
	private String anonymousImageName;
	@JsonProperty("roomId")
	private int roomId;
	
	//역할
	// answerA, answerB, judge 
	private String role;
	
	public void setanonymousId(int anonymousId) {
		this.anonymousId = anonymousId;
	}
	
	public void setanonymousImage(String anonymousImage) {
		this.anonymousImage = anonymousImage;
	}
	
	public void setanonymousNickname(String anonymousNickname) {
		this.anonymousNickname = anonymousNickname;
	}
	
	public void setanonymousImageName(String anonymousImageName) {
		this.anonymousImageName = anonymousImageName;
	}

	public void setroomId(int roomId) {
		this.roomId = roomId;
	}
	
	public void setrole(String role) {
		this.role = role;
	}
}
