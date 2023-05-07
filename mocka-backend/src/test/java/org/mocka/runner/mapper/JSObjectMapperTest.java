package org.mocka.runner.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.script.ScriptEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mocka.configuration.ScriptEngineConfiguration;
import org.mocka.properties.ScriptEngineProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {ObjectMapper.class, ScriptEngineConfiguration.class})
@EnableConfigurationProperties(value = {ScriptEngineProperties.class})
public class JSObjectMapperTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ScriptEngine scriptEngine;

    private JSObjectMapper jsObjectMapper;


    @BeforeEach
    public void setup() {
        jsObjectMapper = new JSObjectMapper(objectMapper, scriptEngine);
    }


    @Test
    public void test() throws JSObjectMapperException {
        var result = jsObjectMapper.map(0);
        assertEquals(0, result);
    }
}
