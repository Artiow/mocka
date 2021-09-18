package org.mocka.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mocka.storage.ScriptStorage;
import org.springframework.stereotype.Service;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import java.io.InputStreamReader;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScriptRunner {

    private static final String ENTRYPOINT = "main";

    private final ScriptEngine engine;
    private final ScriptStorage storage;
    private final JSObjectMapper mapper;


    public synchronized Object invoke(Object arg) throws ScriptRunnerException {
        try {
            try (var reader = new InputStreamReader(storage.getScript())) {
                engine.eval(reader);
            }
            return ((Invocable) engine).invokeFunction(ENTRYPOINT, mapper.map(arg));
        } catch (Exception e) {
            throw new ScriptRunnerException("Exception occurred while script running", e);
        }
    }
}
