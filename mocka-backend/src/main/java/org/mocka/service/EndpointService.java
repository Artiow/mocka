package org.mocka.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mocka.runner.ScriptRunner;
import org.mocka.runner.ScriptRunnerException;
import org.mocka.runner.model.ScriptRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EndpointService {

    private final ScriptRunner scriptRunner;


    public ResponseEntity<Object> handle(
            HttpServletRequest request,
            String subdomain,
            String body,
            Map<String, String> params
    ) {
        try {
            var scriptRequest = new ScriptRequest();
            scriptRequest.setMethod(request.getMethod());
            scriptRequest.setUrl(request.getRequestURL().toString());
            scriptRequest.setSubdomain(subdomain);
            scriptRequest.setUri(request.getRequestURI());
            scriptRequest.setBody(body);
            scriptRequest.setPathVars(Collections.emptyMap());
            scriptRequest.setRequestParams(params);
            var scriptResponse = scriptRunner.invoke(scriptRequest);
            return ResponseEntity.status(scriptResponse.getStatus()).body(scriptResponse.getBody());
        } catch (ScriptRunnerException e) {
            log.error(e.getMessage(), e);
            return null; // todo exception handling
        }
    }
}
