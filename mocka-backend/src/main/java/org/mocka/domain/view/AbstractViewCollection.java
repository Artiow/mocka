package org.mocka.domain.view;

import java.util.List;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

public abstract class AbstractViewCollection<E>
    extends AbstractViewConfiguration<E>
    implements ViewCollection<E> {

    private final MongoTemplate mongoTemplate;


    public AbstractViewCollection(Class<E> viewClass, MongoTemplate mongoTemplate) {
        super(viewClass, mongoTemplate);
        this.mongoTemplate = mongoTemplate;
    }


    public List<E> find(Query query) {
        return mongoTemplate.find(query, getViewClass(), getViewName());
    }
}
