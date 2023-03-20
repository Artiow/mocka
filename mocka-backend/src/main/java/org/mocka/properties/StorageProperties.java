package org.mocka.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("storage")
public class StorageProperties {

    private String bucket;
    private Boolean verifyConnectionOnStartup;
    private Boolean verifyConnectionOnCalls;
}
