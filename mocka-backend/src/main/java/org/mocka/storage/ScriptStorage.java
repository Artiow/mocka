package org.mocka.storage;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScriptStorage {

    @Deprecated(forRemoval = true)
    private static final String SCRIPT = "script";

    private static final String BUCKET = "scripts";
    private static final String JS = ".js";

    private final MinioClient minioClient;


    @PostConstruct
    private void init() {
        try {
            verifyBucket();
        } catch (Exception e) {
            log.error("Failed to connect to minio", e);
        }
    }


    @Deprecated(forRemoval = true)
    public InputStream getScript() throws ScriptStorageException {
        return getScript(SCRIPT);
    }

    public InputStream getScript(String name) throws ScriptStorageException {
        verifyBucket();
        try {
            return minioClient.getObject(
                    GetObjectArgs
                            .builder()
                            .bucket(BUCKET)
                            .object(name + JS)
                            .build()
            );
        } catch (Exception e) {
            throw new ScriptStorageException(String.format("Exception occurred while script \"%s\" getting", name), e);
        }
    }


    @Deprecated(forRemoval = true)
    public void putScript(InputStream stream) throws ScriptStorageException {
        putScript(stream, SCRIPT);
    }

    public void putScript(InputStream stream, String name) throws ScriptStorageException {
        verifyBucket();
        try {
            minioClient.putObject(
                    PutObjectArgs
                            .builder()
                            .stream(stream, stream.available(), -1)
                            .bucket(BUCKET)
                            .object(name + JS)
                            .build()
            );
        } catch (Exception e) {
            throw new ScriptStorageException(String.format("Exception occurred while script \"%s\" putting", name), e);
        }
    }


    private void verifyBucket() throws ScriptStorageException {
        try {
            if (minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET).build())) {
                log.debug(String.format("Bucket \"%s\" is ready", BUCKET));
            } else {
                log.warn(String.format("Bucket \"%s\" does not exist! Trying to create...", BUCKET));
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET).build());
                log.info(String.format("Bucket \"%s\" has been created", BUCKET));
            }
        } catch (Exception e) {
            throw new ScriptStorageException("Failed to connect to minio", e);
        }
    }
}
