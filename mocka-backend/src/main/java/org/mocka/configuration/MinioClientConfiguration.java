package org.mocka.configuration;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.mocka.properties.MinioProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(MinioProperties.class)
public class MinioClientConfiguration {

    private final MinioProperties minio;

    @Bean
    public MinioClient minioClient() {
        return MinioClient
                .builder()
                .endpoint(minio.getEndpoint())
                .credentials(minio.getCredentials().getAccessKey(), minio.getCredentials().getSecretKey())
                .build();
    }
}
