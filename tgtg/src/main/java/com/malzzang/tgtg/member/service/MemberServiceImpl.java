package com.malzzang.tgtg.member.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

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
				memberList.map(m -> new MemberDTO(m.getMemberId(), m.getMemberJoin(), m.getMemberMbti(), m.getMemberRole(), m.getMemberSocial(), m.getMemberStop()));
		
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
	public Page<MemberDTO> searchMemberIdList(Pageable pageable, String memberId) {
		int page = pageable.getPageNumber() - 1;
		int pageLimit = pageable.getPageSize(); // 한페이지 당 보여지는 데이터 개수
		
		Page<Member> memberList = 
				memberRepository.findByMemberIdContaining(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "memberJoin")), memberId);
		Page<MemberDTO> memberDtoList = 
				memberList.map(m -> new MemberDTO(m.getMemberId(), m.getMemberJoin(), m.getMemberMbti(), m.getMemberRole(), m.getMemberSocial(), m.getMemberStop()));
		
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
					memberList.map(m -> new MemberDTO(m.getMemberId(), m.getMemberJoin(), m.getMemberMbti(), m.getMemberRole(), m.getMemberSocial(), m.getMemberStop()));
		} else {
			Page<Member> memberList = 
					memberRepository.findByMemberStopIsNull(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "memberJoin")));
			memberDtoList = 
					memberList.map(m -> new MemberDTO(m.getMemberId(), m.getMemberJoin(), m.getMemberMbti(), m.getMemberRole(), m.getMemberSocial(), m.getMemberStop()));
		}
		return memberDtoList;
	}

	@Override
	public int updateMemberStop(Timestamp memberStop, String memberId) {
		return memberRepository.updateMemberStop(memberStop, memberId);
	}
	
}
