package org.mocka.dto;

import java.time.LocalDateTime;
import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MockServerDto {

    private String id;
    private Collection<MockEndpointDto> endpoints;
    private LocalDateTime createDateTime;
    private LocalDateTime updateDateTime;
}
