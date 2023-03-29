package org.mocka.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
public class MockServerDocument implements MongoDocument {

    @MongoId
    private ObjectId id;
    private Collection<MockEndpointEmbeddedDocument> endpoints;
    private LocalDateTime createDateTime;
    private LocalDateTime updateDateTime;


    public static class MockServerDocumentBuilder {

        private MockServerDocumentBuilder complete() {
            if (this.id == null) {
                this.id(new ObjectId());
            }
            if (this.endpoints == null) {
                this.endpoints(new ArrayList<>());
            }
            if (this.createDateTime == null && this.updateDateTime == null) {
                var now = LocalDateTime.now();
                this.createDateTime(now);
                this.updateDateTime(now);
            }
            return this;
        }

        public MockServerDocument completeAndBuild() {
            return this.complete().build();
        }
    }
}
