package org.mocka.model;

import java.util.ArrayList;
import java.util.List;
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
        .path(new ArrayList<>())
        .build();


    private HttpMethod method;
    private List<String> path;
}
