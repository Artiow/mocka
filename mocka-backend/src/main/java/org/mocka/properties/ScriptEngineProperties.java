package org.mocka.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("script-engine")
public class ScriptEngineProperties {

    private String version;
}
