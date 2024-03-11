package com.malzzang.tgtg.member.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDTO {
	private String memberId;
	private String memberEmail;
	private Date memberJoin;
	private String memberMbti;
	private String memberRole;
	private String memberSocial;
	private Date memberStop;
	
	public MemberDTO(String memberId, String memberEmail, Date memberJoin, String memberMbti, String memberRole, String memberSocial,
			Date memberStop) {
		super();
		this.memberId = memberId;
		this.memberEmail = memberEmail;
		this.memberJoin = memberJoin;
		this.memberMbti = memberMbti;
		this.memberRole = memberRole;
		this.memberSocial = memberSocial;
		this.memberStop = memberStop;
	}
	
	
}
