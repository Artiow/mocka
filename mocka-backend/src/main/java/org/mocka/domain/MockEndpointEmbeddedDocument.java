package org.mocka.domain;

import java.util.ArrayList;
import java.util.List;
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
    private List<String> path;


    @SuppressWarnings("unused")
    public static class MockEndpointEmbeddedDocumentBuilder {

        private MockEndpointEmbeddedDocumentBuilder complete() {
            if (this.id == null) {
                this.id(UUID.randomUUID());
            }
            if (this.path == null) {
                this.path(new ArrayList<>());
            }
            return this;
        }

        public MockEndpointEmbeddedDocument completeAndBuild() {
            return this.complete().build();
        }
    }
}
