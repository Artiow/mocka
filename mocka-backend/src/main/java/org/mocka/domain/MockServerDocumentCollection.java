package org.mocka.domain;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MockServerDocumentCollection extends MongoRepository<MockServerDocument, ObjectId> {

}
