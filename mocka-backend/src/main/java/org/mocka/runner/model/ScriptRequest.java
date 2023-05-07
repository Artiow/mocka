package org.mocka.runner.model;


import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScriptRequest {

    private UUID mockServerId;
    private UUID mockEndpointId;
    private String method;
    private String url;
    private String uri;
    private Map<String, String[]> requestParams;
    private Map<String, String> pathVars;
}
