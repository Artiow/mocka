package org.mocka.domain.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@Document("server")
public class MockServerEntity {

    @MongoId
    @Setter(AccessLevel.NONE)
    private ObjectId id;
    private Collection<MockEndpointEntity> endpoints;
    private LocalDateTime createDateTime;
    private LocalDateTime updateDateTime;


    @Builder
    private MockServerEntity(
            Collection<MockEndpointEntity> endpoints,
            LocalDateTime createDateTime,
            LocalDateTime updateDateTime
    ) {
        this.endpoints = Optional.ofNullable(endpoints).orElse(Collections.emptyList());
        this.createDateTime = createDateTime;
        this.updateDateTime = updateDateTime;
    }
}
