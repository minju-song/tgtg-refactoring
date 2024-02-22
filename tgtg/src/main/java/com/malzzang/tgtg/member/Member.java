package com.malzzang.tgtg.member;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Member {
	
	@Id
    @Column(name="member_id")
	private String memberId;
	
	@Column(name="member_email")
	private String memberEmail;
	
	@Column(name="member_social")
	private String memberSocial;
	
	@Column(name="member_mbti")
	private String memberMbti;
	
	@Column(name="member_win")
	private int memberWin;
	
	@Column(name="member_draw")
	private int memberDraw;
	
	@Column(name="member_lose")
	private int memberLose;
	
	@CreationTimestamp
	@Column(name="member_join")
	private Date memberJoin;
	
	@Column(name="member_stop")
	private Date memberStop;
	
	@Column(name="member_role")
	private String memberRole; //ROLE_GUEST, ROLE_USER, ROLE_ADMIN

	// Builder 패턴 활용 
	@Builder
	public Member(String memberId, String memberEmail, String memberSocial, Date memberJoin, String memberRole) {
		this.memberId = memberId;
		this.memberEmail = memberEmail;
		this.memberSocial = memberSocial;
		this.memberJoin = memberJoin;
		this.memberRole = memberRole;
	}
	
	
}
