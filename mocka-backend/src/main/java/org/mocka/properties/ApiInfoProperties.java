package org.mocka.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("api-info")
public class ApiInfoProperties {

    private String title;
    private String description;
    private String version;
}
