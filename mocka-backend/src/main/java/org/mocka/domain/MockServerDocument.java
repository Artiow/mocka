package org.mocka.domain;

import java.time.LocalDateTime;
import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
@Builder
@Document("server")
public class MockServerDocument {

    @MongoId
    private ObjectId id;
    private Collection<MockEndpoint> endpoints;
    private LocalDateTime createDateTime;
    private LocalDateTime updateDateTime;


    @Getter
    @Setter
    @Builder
    public static class MockEndpoint {

        private ObjectId id;
        private String method;
        private String path;
    }
}
