package org.mocka.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;

@Getter
@Setter
@Builder
public class MockEndpointSettings {

    public final static MockEndpointSettings DEFAULT = MockEndpointSettings.builder()
        .method(HttpMethod.GET)
        .pathPattern("/**")
        .build();


    private HttpMethod method;
    private String pathPattern;
}
