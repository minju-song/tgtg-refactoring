package com.malzzang.tgtg.member.domain;

import java.sql.Timestamp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface MemberRepository extends JpaRepository<Member, String> {

	// findBy(컬럼명)Containing : 컬럼에서 키워드가 포함된 것을 찾겠다
	Page<Member> findByMemberEmailContaining(Pageable pageable, String memberEmail);
	
	Page<Member> findByMemberStopIsNull(Pageable pageable);
	
	Page<Member> findByMemberStopIsNotNull(Pageable pageable);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE Member m SET m.memberStop = :memberStop WHERE m.memberId = :memberId")
	int updateMemberStop(Timestamp memberStop, String memberId);

	// SELECT * FROM member WHERE member_id = ?
	Member findByMemberId(String memberId);
	
	// MBTI 업데이트 
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE Member m SET m.memberMbti = :memberMbti, m.memberRole = :memberRole WHERE m.memberId = :memberId")
	int updateMemberMbti(String memberId, String memberRole, String memberMbti);
	
	// DELETE member WHERE member_id = ?
	
	
	// 승리 + 1
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE Member m SET m.memberWin = :memberWin WHERE m.memberId = :memberId")
	int updateMemberWin(String memberId, int memberWin);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE Member m SET m.memberDraw = :memberDraw WHERE m.memberId = :memberId")
	int updateMemberDraw(String memberId, int memberDraw);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE Member m SET m.memberLose = :memberLose WHERE m.memberId = :memberId")
	int updateMemberLose(String memberId, int memberLose);
}
