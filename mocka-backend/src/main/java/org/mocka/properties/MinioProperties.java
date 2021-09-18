package org.mocka.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("minio")
public class MinioProperties {

    private String endpoint;
    private Credentials credentials;

    @Getter
    @Setter
    public static class Credentials {

        private String accessKey;
        private String secretKey;
    }
}
