package org.mocka.service;

import org.mocka.storage.ScriptStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScriptService {

    private final ScriptStorage storage;
    private final ScriptEngine engine;


    public void upload(Integer id, String script) {
        try {
            engine.eval(script);
            try (InputStream stream = new ByteArrayInputStream(script.getBytes())) {
                storage.putScript(stream);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
