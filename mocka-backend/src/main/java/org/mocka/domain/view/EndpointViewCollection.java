package org.mocka.domain.view;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class EndpointViewCollection extends AbstractViewCollection<EndpointView> {

    public EndpointViewCollection(MongoTemplate mongoTemplate) {
        super(EndpointView.class, mongoTemplate);
    }
}
