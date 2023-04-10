package org.mocka.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MockEndpointPathEmbeddedDocument {

    private String template;
    private String regex;
    private List<String> keys;


    @SuppressWarnings("unused")
    public static class MockEndpointPathEmbeddedDocumentBuilder {

        private MockEndpointPathEmbeddedDocumentBuilder complete() {
            if (this.keys == null) {
                this.keys(new ArrayList<>());
            }
            return this;
        }

        public MockEndpointPathEmbeddedDocument completeAndBuild() {
            return this.complete().build();
        }
    }
}
