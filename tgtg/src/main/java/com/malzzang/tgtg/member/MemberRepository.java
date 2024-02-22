package com.malzzang.tgtg.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {
	
	// SELECT * FROM member WHERE member_id = ?
	Member findByMemberId(String memberId);
	
	//Optional<Member> findByMemberId(String memberId);
}
