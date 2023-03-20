package org.mocka.storage;

import io.minio.errors.ErrorResponseException;
import lombok.extern.slf4j.Slf4j;
import org.mocka.properties.StorageProperties;
import org.mocka.service.MinioService;
import org.mocka.util.ResourceFileUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
public class ScriptStorage {

    private final MinioService minioService;

    private final String bucketName;
    private final boolean verifyConnectionOnStartup;
    private final boolean verifyConnectionOnCalls;


    public ScriptStorage(MinioService minioService, StorageProperties storageProperties) {
        this.minioService = minioService;
        this.bucketName = storageProperties.getBucket();
        this.verifyConnectionOnStartup = storageProperties.getVerifyConnectionOnStartup();
        this.verifyConnectionOnCalls = storageProperties.getVerifyConnectionOnCalls();
    }

    @PostConstruct
    private void init() throws ScriptStorageException {
        if (verifyConnectionOnStartup) {
            verifyBucket(true);
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
        if (verifyConnectionOnCalls) {
            verifyBucket();
        }
        try {
            return minioService.getObject(bucketName, name) != null;
        } catch (Exception e) {
            if (e instanceof ErrorResponseException && "NoSuchKey".equals(((ErrorResponseException) e).errorResponse().code())) {
                // the only way to check object existence due Amazon S3 specification
                return false;
            } else {
                throw new ScriptStorageException(String.format("Exception occurred while script \"%s\" existence checking", name), e);
            }
        }
    }

    public InputStream getScript(String name) throws ScriptStorageException {
        if (verifyConnectionOnCalls) {
            verifyBucket();
        }
        try {
            return minioService.getObject(bucketName, name);
        } catch (Exception e) {
            if (e instanceof ErrorResponseException && "NoSuchKey".equals(((ErrorResponseException) e).errorResponse().code())) {
                // the only way to check object existence due Amazon S3 specification
                throw new ScriptStorageException(String.format("Script \"%s\" does not exist", name), e);
            } else {
                throw new ScriptStorageException(String.format("Exception occurred while script \"%s\" getting", name), e);
            }
        }
    }

    public void putScript(InputStream stream, String name) throws ScriptStorageException {
        if (verifyConnectionOnCalls) {
            verifyBucket();
        }
        try {
            minioService.putObject(stream, bucketName, name);
        } catch (Exception e) {
            throw new ScriptStorageException(String.format("Exception occurred while script \"%s\" putting", name), e);
        }
    }


    private void verifyBucket() throws ScriptStorageException {
        verifyBucket(false);
    }

    private void verifyBucket(boolean orCreate) throws ScriptStorageException {
        try {
            if (minioService.bucketExists(bucketName)) {
                log.debug(String.format("Bucket \"%s\" is ready", bucketName));
            } else if (orCreate) {
                log.warn(String.format("Bucket \"%s\" does not exist! Trying to create...", bucketName));
                minioService.makeBucket(bucketName);
                log.info(String.format("Bucket \"%s\" has been created", bucketName));
            } else {
                throw new ScriptStorageException(String.format("Bucket \"%s\" does no longer exists", bucketName));
            }
        } catch (Exception e) {
            throw new ScriptStorageException("Failed to connect to minio", e);
        }
    }
}
