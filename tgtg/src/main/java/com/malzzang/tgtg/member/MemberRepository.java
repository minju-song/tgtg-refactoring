package com.malzzang.tgtg.member;

import java.sql.Timestamp;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface MemberRepository extends JpaRepository<Member, String> {

	// findBy(컬럼명)Containing : 컬럼에서 키워드가 포함된 것을 찾겠다
	Page<Member> findByMemberIdContaining(Pageable pageable, String memberId);
	
	Page<Member> findByMemberStopIsNull(Pageable pageable);
	
	Page<Member> findByMemberStopIsNotNull(Pageable pageable);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE Member m SET m.memberStop = :memberStop WHERE m.memberId = :memberId")
	int updateMemberStop(Timestamp memberStop, String memberId);

	// SELECT * FROM member WHERE member_id = ?
	Member findByMemberId(String memberId);
	
	//Optional<Member> findByMemberId(String memberId);

}
