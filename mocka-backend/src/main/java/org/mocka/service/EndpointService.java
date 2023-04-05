package org.mocka.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mocka.domain.view.EndpointView;
import org.mocka.domain.view.EndpointViewCollection;
import org.mocka.runner.ScriptRunner;
import org.mocka.runner.ScriptRunnerException;
import org.mocka.runner.model.ScriptRequest;
import org.mocka.storage.ScriptStorage;
import org.mocka.storage.ScriptStorageException;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EndpointService {

    private static final String ENTRYPOINT = "main";

    private final EndpointViewCollection endpointViewCollection;
    private final ScriptStorage scriptStorage;
    private final ScriptRunner scriptRunner;


    public ResponseEntity<Object> handle(
        HttpServletRequest request,
        UUID mockServerId,
        Map<String, Object> params
    ) {
        var requestMethod = HttpMethod.resolve(request.getMethod());
        var requestPath = Collections.<String>emptyList();

        var endpoint = findEndpoint(mockServerId, requestMethod, requestPath);
        var mockEndpointId = endpoint.getEndpoint().getId();

        var scriptRequest = new ScriptRequest();
        scriptRequest.setMockServerId(mockServerId);
        scriptRequest.setMockEndpointId(mockEndpointId);
        scriptRequest.setMethod(request.getMethod());
        scriptRequest.setUrl(request.getRequestURL().toString());
        scriptRequest.setUri(request.getRequestURI());
        scriptRequest.setRequestParams(params);
        try {
            var scriptResponse = scriptRunner.run(
                scriptStorage.getScript(mockEndpointId.toString()),
                ENTRYPOINT,
                scriptRequest
            );
            return ResponseEntity.status(scriptResponse.getStatus()).body(scriptResponse.getBody());
        } catch (ScriptStorageException | ScriptRunnerException e) {
            log.error(e.getMessage(), e);
            return null; // todo exception handling
        }
    }

    public EndpointView findEndpoint(
        UUID mockServerId,
        HttpMethod requestMethod,
        List<String> requestPath
    ) {
        var query = new Query();
        query.addCriteria(Criteria.where("server.id").is(mockServerId));
        query.addCriteria(Criteria.where("endpoint.method").is(requestMethod));
        return endpointViewCollection.find(query).get(0); // todo endpoint search
    }
}
