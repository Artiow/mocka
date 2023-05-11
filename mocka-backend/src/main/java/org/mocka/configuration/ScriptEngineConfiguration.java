package org.mocka.configuration;

import static org.mocka.util.Formatter.format;

import javax.script.ScriptEngine;
import lombok.RequiredArgsConstructor;
import org.mocka.properties.ScriptEngineProperties;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ScriptEngineConfiguration {

    private final ScriptEngineProperties scriptEngine;

    @Bean
    public ScriptEngine scriptEngine() {
        return new NashornScriptEngineFactory().getScriptEngine(
            format("--language={}", scriptEngine.getVersion()),
            "--no-syntax-extensions",
            "--no-java");
    }
}
