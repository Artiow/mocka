package org.mocka.domain.view;

import com.mongodb.client.MongoDatabase;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

@Getter(AccessLevel.PROTECTED)
public abstract class AbstractViewConfiguration<E> {

    private final String viewName;
    private final String viewOn;


    public AbstractViewConfiguration(MongoTemplate mongoTemplate) {
        var view = ViewUtils.extractView(getViewClass());
        this.viewName = view.viewName();
        this.viewOn = view.viewOn();
        createView(mongoTemplate);
    }


    private void createView(MongoTemplate mongoTemplate) {
        mongoTemplate.execute(this::doCreateView);
    }

    private Boolean doCreateView(MongoDatabase db) {
        var pipeline = getPipeline().stream().map(Document::new).collect(Collectors.toList());
        db.getCollection(getViewName()).drop();
        db.createView(getViewName(), getViewOn(), pipeline);
        return true;
    }


    protected abstract Class<E> getViewClass();

    protected abstract List<Map<String, Object>> getPipeline();
}
