package com.malzzang.tgtg.member.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.malzzang.tgtg.member.Member;
import com.malzzang.tgtg.member.MemberRepository;
import com.malzzang.tgtg.member.dto.MemberDTO;

@Service
public class MemberServiceImpl implements MemberService {

	@Autowired
	MemberRepository memberRepository;
	
	@Override
	public Page<MemberDTO> selectMemberList(Pageable pageable) {
		
		int page = pageable.getPageNumber() - 1;
		int pageLimit = pageable.getPageSize(); // 한페이지 당 보여지는 데이터 개수
		
		// page 위치에 있는 값은 0부터 시작
		// 한페이지당 5개씩 보여주고 정렬기준은 memberJoin 기준으로 내림차순 정렬
		Page<Member> memberList = 
				memberRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "memberJoin")));
		Page<MemberDTO> memberDtoList = 
				memberList.map(m -> new MemberDTO(m.getMemberId(), m.getMemberEmail(), m.getMemberJoin(), m.getMemberMbti(), m.getMemberRole(), m.getMemberSocial(), m.getMemberStop()));
		
		return memberDtoList;
	}
	
	@Override
	public Map<String, Object> getPagingInfo(Pageable pageable, Page<MemberDTO> memberDtoList) {
		Map<String, Object> pagingInfo = new HashMap<String, Object>();
		
		int blockLimit = 10;
		int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1; // 1, 11, 21 ...
		int endPage = ((startPage + blockLimit - 1) < memberDtoList.getTotalPages()) ? startPage + blockLimit - 1 : memberDtoList.getTotalPages();
		
		pagingInfo.put("startPage", startPage);
		pagingInfo.put("endPage", endPage);
		
		return pagingInfo;
	}

	@Override
	public Page<MemberDTO> searchMemberEmailList(Pageable pageable, String memberId) {
		int page = pageable.getPageNumber() - 1;
		int pageLimit = pageable.getPageSize(); // 한페이지 당 보여지는 데이터 개수
		
		Page<Member> memberList = 
				memberRepository.findByMemberEmailContaining(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "memberJoin")), memberId);
		Page<MemberDTO> memberDtoList = 
				memberList.map(m -> new MemberDTO(m.getMemberId(), m.getMemberEmail(), m.getMemberJoin(), m.getMemberMbti(), m.getMemberRole(), m.getMemberSocial(), m.getMemberStop()));
		
		return memberDtoList;
	}

	@Override
	public Page<MemberDTO> searchMemberStopList(Pageable pageable, String memberStop) {
		int page = pageable.getPageNumber() - 1;
		int pageLimit = pageable.getPageSize(); // 한페이지 당 보여지는 데이터 개수
		
		Page<MemberDTO> memberDtoList = null;
		if(memberStop.equals("stop")) {
			Page<Member> memberList = 
					memberRepository.findByMemberStopIsNotNull(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "memberJoin")));
			memberDtoList = 
					memberList.map(m -> new MemberDTO(m.getMemberId(), m.getMemberEmail(), m.getMemberJoin(), m.getMemberMbti(), m.getMemberRole(), m.getMemberSocial(), m.getMemberStop()));
		} else {
			Page<Member> memberList = 
					memberRepository.findByMemberStopIsNull(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "memberJoin")));
			memberDtoList = 
					memberList.map(m -> new MemberDTO(m.getMemberId(), m.getMemberEmail(), m.getMemberJoin(), m.getMemberMbti(), m.getMemberRole(), m.getMemberSocial(), m.getMemberStop()));
		}
		return memberDtoList;
	}

	@Override
	public int updateMemberStop(Timestamp memberStop, String memberId) {
		return memberRepository.updateMemberStop(memberStop, memberId);
	}

	
	// MBTI 검사 후 업데이트
	@Override
	public Map<String, Object> updateMemberMbti(String memberId, String memberRole, String memberMbti) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		boolean isSuccessed = false;		
		int afterUpdate = 0;		
		
		if (memberRole.toUpperCase().equals("ROLE_GUEST")) {
			// 회원가입 후 MBTI 검사
			memberRole = "ROLE_USER";
			afterUpdate = memberRepository.updateMemberMbti(memberId, memberRole, memberMbti);
			result.put("checkFirst", "first");
		} else {
			// 마이페이지에서 MBTI 재검사 
			afterUpdate = memberRepository.updateMemberMbti(memberId, memberRole, memberMbti);
			result.put("checkFirst", "again");
		}
		
		if (afterUpdate == 1) {
			isSuccessed = true;
		}
		
		result.put("result", isSuccessed);
		System.out.println("MBTI 업데이트 디비 변경 후 === " + afterUpdate);
		System.out.println("MBTI 업데이트 디비 변경 후 === " + result);
		
		return result;
	}
	
	// mypage member READ
	@Override
	public MemberDTO selectMemberInfo(String memberId) {
		MemberDTO memberInfo = null;
		
		Member member = memberRepository.findByMemberId(memberId);
		memberInfo = 
				new MemberDTO(member.getMemberEmail(), member.getMemberMbti(), member.getMemberWin(), 
							  member.getMemberDraw(), member.getMemberLose(), member.getMemberSocial());
		return memberInfo;
	}

	// mypage member Withdrawal
	@Override
	public boolean deleteMember(String memberId) {
		memberRepository.deleteById(memberId);
		boolean result = false;
		
		Optional<Member> temp = memberRepository.findById(memberId);
		if (temp.isEmpty()) {
			result = true;
		}
		System.out.println("삭제 후"+result);
		return result;
	}
	
	// main page RNAK
	@Override
	public Page<MemberDTO> selectMemberWin() {
		
		Page<Member> memberList = 
				memberRepository.findAll(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "memberWin")));
		Page<MemberDTO> memberDtoList = 
				memberList.map(m -> new MemberDTO(m.getMemberEmail(), m.getMemberWin()));
		
		return memberDtoList;
	}
	
	public int increaseWin(String memberId) {
	    Member member = memberRepository.findById(memberId)
	            .orElseThrow(() -> new IllegalArgumentException("해당 ID의 멤버가 존재하지 않습니다. ID: " + memberId));
	    
	    int win = member.getMemberWin() + 1;
	    
	    return memberRepository.updateMemberWin(memberId, win);
	}

	@Override
	public int increaseLose(String memberId) {
		Member member = memberRepository.findById(memberId)
	            .orElseThrow(() -> new IllegalArgumentException("해당 ID의 멤버가 존재하지 않습니다. ID: " + memberId));
	    
	    int lose = member.getMemberLose() + 1;
	    
	    return memberRepository.updateMemberLose(memberId, lose);
	}

	@Override
	public int increaseDraw(String memberId) {
		System.out.println("아이디!!!"+memberId);
		Member member = memberRepository.findById(memberId)
	            .orElseThrow(() -> new IllegalArgumentException("해당 ID의 멤버가 존재하지 않습니다. ID: " + memberId));
	    
	    int draw = member.getMemberDraw() + 1;
	    
	    return memberRepository.updateMemberDraw(memberId, draw);
	}
	
}
