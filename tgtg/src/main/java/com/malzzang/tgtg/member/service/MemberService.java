package com.malzzang.tgtg.member.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.malzzang.tgtg.member.dto.MemberDTO;

public interface MemberService {
	
//	public Page<MemberDTO> selectMemberList(Pageable pageable);
//	public Map<String, Object> getPagingInfo(Pageable pageable, Page<MemberDTO> memberDtoList); // 회원목록 페이징
//	public Page<MemberDTO> searchMemberEmailList(Pageable pageable, String memberId); // 검색리스트(회원이메일)
//	public Page<MemberDTO> searchMemberStopList(Pageable pageable, String memberStop); // 검색리스트(계정정지유무)
//	public int updateMemberStop(Timestamp memberStop, String memberId);
	
	// MBTI 검사 후 업데이트
	public Map<String, Object> updateMemberMbti(String memberId, String memberRole, String memberMbti);
	
	// mypage member READ
	public MemberDTO selectMemberInfo(String memberId);
	
	// mypage membert Withdrawal
	public boolean deleteMember(String memberId);
	
	// main page RANK
	public Page<MemberDTO> selectMemberWin();
	
	// 승리업데이트
	public int increaseWin(String memberId);
	
	// 패배업데이트
	public int increaseLose(String memberId);
	
	// 무승부업데이트
	public int increaseDraw(String memberId);
}
