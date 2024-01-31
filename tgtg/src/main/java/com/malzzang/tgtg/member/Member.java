package com.malzzang.tgtg.member;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Member {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="member_id")
	private String memberId;
	
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
	
	@Column(name="member_join")
	private Date memberJoin;
	
	@Column(name="member_stop")
	private Date memberStop;
	
	@Column(name="member_role")
	private String memberRole;
}
