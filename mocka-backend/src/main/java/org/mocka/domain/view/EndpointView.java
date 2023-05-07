package org.mocka.domain.view;

import java.util.UUID;
import lombok.Getter;

@Getter
@SuppressWarnings("unused")
@View(viewName = "endpoint", viewOn = "server")
public class EndpointView {

    private Server server;
    private Endpoint endpoint;


    @Getter
    public static class Server {

        private UUID id;
    }

    @Getter
    public static class Endpoint {

        private UUID id;
        private String method;
        private String pathPattern;
    }
}
