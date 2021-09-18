package org.mocka.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.mocka.storage.ScriptStorage;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.script.ScriptEngine;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScriptService {

    private final ScriptStorage storage;
    private final ScriptEngine engine;


    public String getSample() {
        try (var scriptStream = new FileInputStream(ResourceUtils.getFile("classpath:sample.js"))) {
            return IOUtils.toString(scriptStream, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }


    public String getScript(Integer id) {
        try (var scriptStream = storage.getScript()) {
            return IOUtils.toString(scriptStream, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.info("Sample returning");
            return getSample();
        }
    }

    public void uploadScript(Integer id, String script) {
        try {
            engine.eval(script);
            try (var scriptStream = new ByteArrayInputStream(script.getBytes(StandardCharsets.UTF_8))) {
                storage.putScript(scriptStream);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
