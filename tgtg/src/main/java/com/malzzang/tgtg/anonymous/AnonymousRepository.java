package com.malzzang.tgtg.anonymous;

import org.springframework.data.jpa.repository.JpaRepository;

import com.malzzang.tgtg.subject.Subject;

public interface AnonymousRepository extends JpaRepository<Subject, Integer> {

}
