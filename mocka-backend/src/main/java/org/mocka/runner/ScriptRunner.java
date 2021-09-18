package org.mocka.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mocka.runner.mapper.JSObjectMapper;
import org.mocka.runner.model.ScriptRequest;
import org.mocka.runner.model.ScriptResponse;
import org.mocka.storage.ScriptStorage;
import org.springframework.stereotype.Service;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import java.io.InputStreamReader;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScriptRunner {

    @Deprecated(forRemoval = true)
    private static final String ENTRYPOINT = "main";

    private final ScriptEngine engine;
    private final ScriptStorage storage;
    private final JSObjectMapper mapper;


    @Deprecated(forRemoval = true)
    public synchronized ScriptResponse invoke(ScriptRequest request) throws ScriptRunnerException {
        return invoke(null, ENTRYPOINT, request);
    }

    public synchronized ScriptResponse invoke(String script, String entrypoint, ScriptRequest request) throws ScriptRunnerException {
        try {
            try (var reader = new InputStreamReader(storage.getScript())) {
                engine.eval(reader);
            }
            var body = ((Invocable) engine).invokeFunction(entrypoint, mapper.map(request));
            var scriptResponse = new ScriptResponse();
            scriptResponse.setStatus(200);
            scriptResponse.setBody(body);
            return scriptResponse;
        } catch (Exception e) {
            throw new ScriptRunnerException("Exception occurred while script running", e);
        }
    }
}
