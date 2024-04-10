package com.malzzang.tgtg.anonymous;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.malzzang.tgtg.subject.Subject;

public interface AnonymousRepository extends MongoRepository<Anonymous, String> {

	Anonymous findByAnonymousId(int anonymousId);
}
