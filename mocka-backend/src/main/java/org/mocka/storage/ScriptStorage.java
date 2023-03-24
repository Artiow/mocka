package org.mocka.storage;

import io.minio.errors.ErrorResponseException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mocka.properties.StorageProperties;
import org.mocka.service.MinioService;
import org.mocka.util.ResourceFileUtils;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScriptStorage {

    private final MinioService minioService;
    private final TaskScheduler taskScheduler;
    private final StorageProperties storageProperties;


    @PostConstruct
    private void init() throws ScriptStorageException {
        if (storageProperties.getVerifyConnectionOnStartup()) {
            verifyBucket(true);
        }
        if (storageProperties.getVerifyConnectionOnSchedule()) {
            var rate = storageProperties.getVerifyConnectionOnScheduleRate();
            var startTime = Instant.now().plus(rate);
            taskScheduler.scheduleAtFixedRate(this::verifyBucketOnSchedule, startTime, rate);
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
        if (storageProperties.getVerifyConnectionOnCalls()) {
            verifyBucket();
        }
        try (var script = minioService.getObject(getBucketName(), name)) {
            return script != null;
        } catch (Exception e) {
            if (isExceptionNoSuckKey(e)) {
                // the only way to check object existence due Amazon S3 specification
                return false;
            } else {
                throw new ScriptStorageException(
                    "Exception occurred while script \"{}\" existence checking", name, e);
            }
        }
    }

    public InputStream getScript(String name) throws ScriptStorageException {
        if (storageProperties.getVerifyConnectionOnCalls()) {
            verifyBucket();
        }
        try {
            return minioService.getObject(getBucketName(), name);
        } catch (Exception e) {
            if (isExceptionNoSuckKey(e)) {
                throw new ScriptStorageException(
                    "Script \"{}\" does not exist", name, e);
            } else {
                throw new ScriptStorageException(
                    "Exception occurred while script \"{}\" getting", name, e);
            }
        }
    }


    public void putSampleAs(String name) throws ScriptStorageException {
        try (var sample = getSample()) {
            putScript(sample, name);
        } catch (IOException e) {
            // todo: handle an exception that occurs when stream closing
        }
    }

    public void putScript(InputStream stream, String name) throws ScriptStorageException {
        if (storageProperties.getVerifyConnectionOnCalls()) {
            verifyBucket();
        }
        try {
            minioService.putObject(stream, getBucketName(), name);
        } catch (Exception e) {
            throw new ScriptStorageException(
                "Exception occurred while script \"{}\" putting", name, e);
        }
    }


    private boolean isExceptionNoSuckKey(Exception e) {
        return e instanceof ErrorResponseException
            && "NoSuchKey".equals(((ErrorResponseException) e).errorResponse().code());
    }


    private void verifyBucketOnSchedule() {
        try {
            verifyBucket();
        } catch (ScriptStorageException e) {
            log.error("Exception occurred while scheduled bucket verifying", e);
        }
    }

    private void verifyBucket() throws ScriptStorageException {
        verifyBucket(false);
    }

    private void verifyBucket(boolean orCreate) throws ScriptStorageException {
        try {
            if (minioService.bucketExists(getBucketName())) {
                log.debug("Bucket \"{}\" is ready", getBucketName());
            } else if (orCreate) {
                log.warn("Bucket \"{}\" does not exist! Trying to create...", getBucketName());
                minioService.makeBucket(getBucketName());
                log.info("Bucket \"{}\" has been created", getBucketName());
            } else {
                throw new ScriptStorageException(
                    "Bucket \"{}\" does no longer exists", getBucketName());
            }
        } catch (Exception e) {
            throw new ScriptStorageException(
                "Bucket \"{}\" verifying failed", getBucketName(), e);
        }
    }


    private String getBucketName() {
        return storageProperties.getBucket();
    }
}
