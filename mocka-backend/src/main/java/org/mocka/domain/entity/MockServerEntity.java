package org.mocka.domain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

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
        this.endpoints = Optional.ofNullable(endpoints).orElse(new ArrayList<>());
        this.createDateTime = createDateTime;
        this.updateDateTime = updateDateTime;
    }
}
