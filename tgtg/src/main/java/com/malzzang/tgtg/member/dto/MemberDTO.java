package com.malzzang.tgtg.member.dto;

import java.util.Date;

import lombok.Getter;

@Getter
public class MemberDTO {
	private String memberId;
	private Date memberJoin;
	private String memberMbti;
	private String memberRole;
	private String memberSocial;
	private Date memberStop;
	
	public MemberDTO(String memberId, Date memberJoin, String memberMbti, String memberRole, String memberSocial,
			Date memberStop) {
		super();
		this.memberId = memberId;
		this.memberJoin = memberJoin;
		this.memberMbti = memberMbti;
		this.memberRole = memberRole;
		this.memberSocial = memberSocial;
		this.memberStop = memberStop;
	}
	
	
}
