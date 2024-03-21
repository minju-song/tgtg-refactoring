package com.malzzang.tgtg.anonymous.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class AnonymousDTO {
	private int anonymousId;
	private String anonymousImage;
	private String anonymousNickname;
	private String anonymousImageName;
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
