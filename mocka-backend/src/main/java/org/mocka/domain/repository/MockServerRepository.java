package org.mocka.domain.repository;

import org.mocka.domain.entity.MockServerEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MockServerRepository extends MongoRepository<MockServerEntity, String> {

}
