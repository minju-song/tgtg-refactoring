package com.malzzang.tgtg.member;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Member {
	
	@Id
    @Column(name="member_id")
	private String memberId;
	
	@Column(name="member_social")
	private String memberSocial;
	
	@Column(name="member_mbti")
	private String memberMbti;
	
	@Column(name="member_win")
	private Integer memberWin;
	
	@Column(name="member_draw")
	private Integer memberDraw;
	
	@Column(name="member_lose")
	private Integer memberLose;
	
	@Column(name="member_join")
	private Date memberJoin;
	
	@Column(name="member_stop")
	private Date memberStop;
	
	@Column(name="member_role")
	private String memberRole;
}
