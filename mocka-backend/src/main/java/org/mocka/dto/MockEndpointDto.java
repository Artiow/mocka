package org.mocka.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MockEndpointDto {

    private String id;
    private String method;
    private String path;
}
