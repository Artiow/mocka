package org.mocka.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties("storage")
public class StorageProperties {

    private String bucket;
    private Boolean verifyConnectionOnStartup;
    private Boolean verifyConnectionOnCalls;
    private Boolean verifyConnectionOnSchedule;
    private Duration verifyConnectionOnScheduleRate;
}
