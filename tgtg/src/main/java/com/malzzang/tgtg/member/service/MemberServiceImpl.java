package com.malzzang.tgtg.member.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.malzzang.tgtg.member.domain.Member;
import com.malzzang.tgtg.member.domain.MemberRepository;
import com.malzzang.tgtg.member.dto.MemberDTO;

@Service
public class MemberServiceImpl implements MemberService {

	@Autowired
	MemberRepository memberRepository;

	
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

	@Override
	public String getJWT(String memberId) {

		int JWT_EXPIRATION_TIME = 7200000;
		String JWT_SECRET = "jgeijowjfoiwfjwoifjoweifjowefjwofjwoifjweofijweofijwfoeiwjfoiewfjowifjweoifhwoefhweoifjow";
		String token = Jwts.builder()
				.setSubject(memberId) // 예: 사용자 이름
				.setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_TIME)) // 토큰 유효 시간
				.signWith(SignatureAlgorithm.HS512, JWT_SECRET) // 서명 알고리즘 및 비밀키
				.compact();

		return token;
	}

}
