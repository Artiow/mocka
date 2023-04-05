package org.mocka.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
@Builder
@Document("server")
public class MockServerDocument {

    @MongoId
    private UUID id;
    private Collection<MockEndpointEmbeddedDocument> endpoints;
    private LocalDateTime createDateTime;
    private LocalDateTime updateDateTime;


    @SuppressWarnings("unused")
    public static class MockServerDocumentBuilder {

        private MockServerDocumentBuilder complete() {
            if (this.id == null) {
                this.id(UUID.randomUUID());
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
