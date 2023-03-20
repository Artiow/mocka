package org.mocka.configuration;

import lombok.RequiredArgsConstructor;
import org.mocka.properties.ScriptEngineProperties;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.script.ScriptEngine;

@Configuration
@RequiredArgsConstructor
public class ScriptEngineConfiguration {

    private final ScriptEngineProperties scriptEngine;

    @Bean
    public ScriptEngine scriptEngine() {
        return new NashornScriptEngineFactory().getScriptEngine(
                String.format("--language=%s", scriptEngine.getVersion())
        );
    }
}
