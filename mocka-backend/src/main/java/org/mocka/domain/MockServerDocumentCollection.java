package org.mocka.domain;

import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MockServerDocumentCollection extends MongoRepository<MockServerDocument, UUID> {

}
