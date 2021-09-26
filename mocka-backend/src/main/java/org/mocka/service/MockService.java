package org.mocka.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.mocka.storage.ScriptStorage;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.mocka.util.DevelopConstants.SCRIPT;

@Slf4j
@Service
@RequiredArgsConstructor
public class MockService {

    private final ScriptStorage storage;
    private final ScriptEngine engine;


    public String getSample() {
        try (var scriptStream = storage.getSample()) {
            return IOUtils.toString(scriptStream, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }


    public String getScript(Integer id) {
        try (var scriptStream = storage.scriptExists(SCRIPT) ? storage.getScript(SCRIPT) : storage.getSample()) {
            return IOUtils.toString(scriptStream, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public void uploadScript(Integer id, String script) {
        try {
            engine.eval(script);
            try (var scriptStream = new ByteArrayInputStream(script.getBytes(StandardCharsets.UTF_8))) {
                storage.putScript(scriptStream, SCRIPT);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
