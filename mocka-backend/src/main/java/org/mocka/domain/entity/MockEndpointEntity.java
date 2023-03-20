package org.mocka.domain.entity;

import lombok.*;
import org.bson.types.ObjectId;

@Getter
@Setter
@NoArgsConstructor
public class MockEndpointEntity {

    @Setter(AccessLevel.NONE)
    private ObjectId id;
    private String method;
    private String path;


    @Builder
    private MockEndpointEntity(String path, String method) {
        this.id = ObjectId.get();
        this.method = method;
        this.path = path;
    }
}
