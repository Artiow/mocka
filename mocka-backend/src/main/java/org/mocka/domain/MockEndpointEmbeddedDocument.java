package org.mocka.domain;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;

@Getter
@Setter
@Builder
public class MockEndpointEmbeddedDocument {

    private UUID id;
    private HttpMethod method;
    private String pathPattern;


    @SuppressWarnings("unused")
    public static class MockEndpointEmbeddedDocumentBuilder {

        private MockEndpointEmbeddedDocumentBuilder complete() {
            if (this.id == null) {
                this.id(UUID.randomUUID());
            }
            return this;
        }

        public MockEndpointEmbeddedDocument completeAndBuild() {
            return this.complete().build();
        }
    }
}
