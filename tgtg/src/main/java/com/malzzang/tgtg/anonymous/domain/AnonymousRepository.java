package com.malzzang.tgtg.anonymous.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AnonymousRepository extends MongoRepository<Anonymous, String> {

	Anonymous findByAnonymousId(int anonymousId);
}
