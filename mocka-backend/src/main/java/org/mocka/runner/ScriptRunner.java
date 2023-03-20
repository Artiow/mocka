package org.mocka.runner;

import lombok.RequiredArgsConstructor;
import org.mocka.runner.mapper.JSObjectMapper;
import org.mocka.runner.model.ScriptRequest;
import org.mocka.runner.model.ScriptResponse;
import org.mocka.storage.ScriptStorage;
import org.openjdk.nashorn.api.scripting.JSObject;
import org.springframework.stereotype.Service;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import java.io.InputStreamReader;

@Service
@RequiredArgsConstructor
public class ScriptRunner {

    private final ScriptStorage storage;
    private final ScriptEngine engine;
    private final JSObjectMapper mapper;


    //todo: make thread-safe
    public synchronized ScriptResponse run(String scriptName, String entrypoint, ScriptRequest request)
            throws ScriptRunnerException {
        try {
            var scriptFile = storage.getScript(scriptName);
            try (var reader = new InputStreamReader(scriptFile)) {
                engine.eval(reader);
            }
            var invocationResult = invokeEntrypoint(entrypoint, mapper.map(request));
            var scriptResponse = new ScriptResponse();
            scriptResponse.setStatus((Integer) invocationResult.getMember("status"));
            scriptResponse.setBody(invocationResult.getMember("body"));
            return scriptResponse;
        } catch (Exception e) {
            throw new ScriptRunnerException("Exception occurred while script running", e);
        }
    }

    private JSObject invokeEntrypoint(String name, JSObject request) throws InvokeException {
        Object response;
        try {
            response = ((Invocable) engine).invokeFunction(name, request);
        } catch (Exception e) {
            throw new InvokeException("Entrypoint function calling caused exception", e);
        }
        if (response == null) {
            throw new InvokeException(InvokeException.Type.RESULT_IS_NULL, "Entrypoint function must not return null or undefined");
        }
        if (!(response instanceof JSObject)) {
            throw new InvokeException(InvokeException.Type.RESULT_IS_NOT_OBJECT, "Entrypoint function must return an JS object");
        }
        return (JSObject) response;
    }
}
