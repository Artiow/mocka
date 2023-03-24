package org.mocka.domain.view;

import lombok.Getter;
import org.bson.types.ObjectId;

@Getter
@View(viewName = "endpoint", viewOn = "server")
public class EndpointView {

    private Server server;
    private Endpoint endpoint;


    @Getter
    public static class Server {

        private ObjectId id;
    }

    @Getter
    public static class Endpoint {

        private ObjectId id;
        private String method;
        private String path;
    }
}
