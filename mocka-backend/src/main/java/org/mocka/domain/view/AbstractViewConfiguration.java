package org.mocka.domain.view;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoDatabase;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.mocka.util.FormatUtils;
import org.mocka.util.ResourceFileUtils;
import org.springframework.data.mongodb.core.MongoTemplate;

@Getter(AccessLevel.PROTECTED)
public abstract class AbstractViewConfiguration<E> {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<List<Map<String, Object>>> PIPELINE_TYPE_REFERENCE = new TypeReference<>() { };


    private final Class<E> viewClass;
    private final String viewName;
    private final String viewOn;


    public AbstractViewConfiguration(Class<E> viewClass, MongoTemplate mongoTemplate) {
        this.viewClass = viewClass;
        var view = ViewUtils.extractViewAnnotation(this.viewClass);
        this.viewName = view.viewName();
        this.viewOn = view.viewOn();
        createView(mongoTemplate);
    }


    private static Boolean doCreateView(MongoDatabase db, String viewName, String viewOn, List<Document> pipeline) {
        db.getCollection(viewName).drop();
        db.createView(viewName, viewOn, pipeline);
        return true;
    }

    private static List<Map<String, Object>> readPipeline(InputStream jsonStream) throws IOException {
        return MAPPER.readValue(IOUtils.toString(jsonStream, StandardCharsets.UTF_8), PIPELINE_TYPE_REFERENCE);
    }


    private void createView(MongoTemplate mongoTemplate) {
        var pipeline = getPipeline().stream().map(Document::new).collect(Collectors.toList());
        mongoTemplate.execute(db -> doCreateView(db, getViewName(), getViewOn(), pipeline));
    }

    @SneakyThrows(IOException.class)
    private List<Map<String, Object>> getPipeline() {
        try (var jsonStream = ResourceFileUtils.open(FormatUtils.format("classpath:view/{}.json", getViewName()))) {
            return readPipeline(jsonStream);
        }
    }
}
