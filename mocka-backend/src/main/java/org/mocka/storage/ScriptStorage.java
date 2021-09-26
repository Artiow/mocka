package org.mocka.storage;

import io.minio.errors.ErrorResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mocka.properties.StorageProperties;
import org.mocka.service.MinioService;
import org.mocka.util.ResourceFileUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(StorageProperties.class)
public class ScriptStorage {

    private final MinioService minioService;
    private final StorageProperties storageProperties;


    @PostConstruct
    private void init() throws ScriptStorageException {
        if (storageProperties.getVerifyConnectionOnStartup()) {
            verifyBucket();
        }
    }


    public InputStream getSample() throws ScriptStorageException {
        try {
            return ResourceFileUtils.open("classpath:sample.js");
        } catch (IOException e) {
            throw new ScriptStorageException("Exception occurred while script sample getting", e);
        }
    }


    public boolean scriptExists(String name) throws ScriptStorageException {
        verifyBucket();
        try {
            return minioService.getObject(getBucketName(), name) != null;
        } catch (Exception e) {
            if (e instanceof ErrorResponseException && "NoSuchKey".equals(((ErrorResponseException) e).errorResponse().code())) {
                // only way to check object existence due Amazon S3 specification
                return false;
            } else {
                throw new ScriptStorageException(String.format("Exception occurred while script \"%s\" existence checking", name), e);
            }
        }
    }

    public InputStream getScript(String name) throws ScriptStorageException {
        verifyBucket();
        try {
            return minioService.getObject(getBucketName(), name);
        } catch (Exception e) {
            throw new ScriptStorageException(String.format("Exception occurred while script \"%s\" getting", name), e);
        }
    }

    public void putScript(InputStream stream, String name) throws ScriptStorageException {
        verifyBucket();
        try {
            minioService.putObject(stream, getBucketName(), name);
        } catch (Exception e) {
            throw new ScriptStorageException(String.format("Exception occurred while script \"%s\" putting", name), e);
        }
    }


    private void verifyBucket() throws ScriptStorageException {
        try {
            if (minioService.bucketExists(getBucketName())) {
                log.debug(String.format("Bucket \"%s\" is ready", getBucketName()));
            } else {
                log.warn(String.format("Bucket \"%s\" does not exist! Trying to create...", getBucketName()));
                minioService.makeBucket(getBucketName());
                log.info(String.format("Bucket \"%s\" has been created", getBucketName()));
            }
        } catch (Exception e) {
            throw new ScriptStorageException("Failed to connect to minio", e);
        }
    }


    private String getBucketName() {
        return storageProperties.getBucket();
    }
}
