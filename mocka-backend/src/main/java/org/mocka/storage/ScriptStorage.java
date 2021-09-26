package org.mocka.storage;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mocka.properties.StorageProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(StorageProperties.class)
public class ScriptStorage {

    private static final String JS = ".js";

    private final MinioClient minioClient;
    private final StorageProperties storageProperties;


    @PostConstruct
    private void init() throws ScriptStorageException {
        if (storageProperties.getVerifyConnectionOnStartup()) {
            verifyBucket();
        }
    }


    public InputStream getScript(String name) throws ScriptStorageException {
        verifyBucket();
        try {
            return minioClient.getObject(
                    GetObjectArgs
                            .builder()
                            .bucket(getBucket())
                            .object(name + JS)
                            .build()
            );
        } catch (Exception e) {
            throw new ScriptStorageException(String.format("Exception occurred while script \"%s\" getting", name), e);
        }
    }

    public void putScript(InputStream stream, String name) throws ScriptStorageException {
        verifyBucket();
        try {
            minioClient.putObject(
                    PutObjectArgs
                            .builder()
                            .stream(stream, stream.available(), -1)
                            .bucket(getBucket())
                            .object(name + JS)
                            .build()
            );
        } catch (Exception e) {
            throw new ScriptStorageException(String.format("Exception occurred while script \"%s\" putting", name), e);
        }
    }


    private void verifyBucket() throws ScriptStorageException {
        try {
            if (minioClient.bucketExists(BucketExistsArgs.builder().bucket(getBucket()).build())) {
                log.debug(String.format("Bucket \"%s\" is ready", getBucket()));
            } else {
                log.warn(String.format("Bucket \"%s\" does not exist! Trying to create...", getBucket()));
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(getBucket()).build());
                log.info(String.format("Bucket \"%s\" has been created", getBucket()));
            }
        } catch (Exception e) {
            throw new ScriptStorageException("Failed to connect to minio", e);
        }
    }

    private String getBucket() {
        return storageProperties.getBucket();
    }
}
