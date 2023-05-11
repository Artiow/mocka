package org.mocka.runner;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mocka.configuration.ScriptEngineConfiguration;
import org.mocka.properties.ScriptEngineProperties;
import org.mocka.util.ResourceFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@TestMethodOrder(MethodName.class)
@SpringBootTest(classes = {ScriptEngineConfiguration.class})
@EnableConfigurationProperties(value = {ScriptEngineProperties.class})
public class ScriptEnginePermissionsTest {

    @Autowired
    private ScriptEngine scriptEngine;


    @Test
    public void testPermissions_1type() throws IOException {
        doTestPermissions("classpath:testPermissions_1type.js");
    }

    @Test
    public void testPermissions_2type() throws IOException {
        doTestPermissions("classpath:testPermissions_2type.js");
    }

    @Test
    public void testPermissions_3type() throws IOException {
        doTestPermissions("classpath:testPermissions_3type.js");
    }


    private void doTestPermissions(String testResourceLocation) throws IOException {
        try (
            final var scriptReader = ResourceFileUtils.read(testResourceLocation)) {
            scriptEngine.eval(scriptReader, new SimpleScriptContext());
        } catch (ScriptException e) {
            final var msg = e.getMessage();
            assertTrue(msg.contains("ReferenceError"));
            return;
        }
        fail();
    }
}
