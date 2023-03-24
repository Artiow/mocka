package org.mocka.domain.view;

import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class EndpointViewCollection extends AbstractViewCollection<EndpointView> {

    public EndpointViewCollection(MongoTemplate mongoTemplate) {
        super(mongoTemplate);
    }


    @Override
    protected Class<EndpointView> getViewClass() {
        return EndpointView.class;
    }

    @Override
    protected List<Map<String, Object>> getPipeline() {
        return Arrays.asList(
            ImmutableMap.of("$unwind", ImmutableMap.of(
                "path", "$endpoints"
            )),
            ImmutableMap.of("$project", ImmutableMap.of(
                "_id", 0,
                "server._id", "$_id",
                "endpoint", "$endpoints"
            ))
        );
    }
}
