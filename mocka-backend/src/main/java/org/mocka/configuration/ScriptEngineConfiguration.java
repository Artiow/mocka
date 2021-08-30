package org.mocka.configuration;

import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.script.ScriptEngine;

@Configuration
public class ScriptEngineConfiguration {

    @Bean
    public ScriptEngine scriptEngine() {
        return new NashornScriptEngineFactory().getScriptEngine("--language=es6");
    }
}
