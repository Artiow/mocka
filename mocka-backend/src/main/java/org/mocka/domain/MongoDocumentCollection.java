package org.mocka.domain;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface MongoDocumentCollection<T extends MongoDocument> extends MongoRepository<T, ObjectId> {

}
