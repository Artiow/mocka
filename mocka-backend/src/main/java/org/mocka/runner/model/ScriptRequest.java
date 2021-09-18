package org.mocka.runner.model;


import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ScriptRequest {

    private String method;
    private String url;
    private String subdomain;
    private String uri;
    private String body;
    private Map<String, String> pathVars;
    private Map<String, String> requestParams;
}
