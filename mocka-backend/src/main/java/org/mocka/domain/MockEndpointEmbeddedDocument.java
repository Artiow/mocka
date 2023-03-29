package org.mocka.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
@Builder
public class MockEndpointEmbeddedDocument implements MongoEmbeddedDocument {

    private ObjectId id;
    private String method;
    private String path;


    public static class MockEndpointEmbeddedDocumentBuilder {

        private MockEndpointEmbeddedDocumentBuilder complete() {
            if (this.id == null) {
                this.id(new ObjectId());
            }
            return this;
        }

        public MockEndpointEmbeddedDocument completeAndBuild() {
            return this.complete().build();
        }
    }
}
