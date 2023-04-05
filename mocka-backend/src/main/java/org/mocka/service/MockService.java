package org.mocka.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.mocka.domain.MockEndpointEmbeddedDocument;
import org.mocka.domain.MockServerDocument;
import org.mocka.domain.MockServerDocumentCollection;
import org.mocka.model.MockEndpointSettings;
import org.mocka.storage.ScriptStorage;
import org.mocka.storage.ScriptStorageException;
import org.mocka.util.UuidUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MockService {

    private final Environment env;
    private final MockServerDocumentCollection collection;
    private final ScriptStorage storage;


    @Transactional
    public UUID createMockServer(boolean stub) {
        try {
            var mockServer = MockServerDocument.builder().completeAndBuild();
            if (stub && Arrays.asList(env.getActiveProfiles()).contains("local")) {
                // stub mock server has empty uuid for ease of testing
                mockServer.setId(UuidUtils.emptyUuid());
            }
            var mockEndpointId = addMockEndpointTo(mockServer, MockEndpointSettings.DEFAULT);
            var mockServerId = collection.insert(mockServer).getId();
            storage.putSampleAs(mockEndpointId.toString());
            return mockServerId;
        } catch (ScriptStorageException e) {
            throw new MockServiceException("Exception occurred while mock server creating", e);
        }
    }

    @Transactional
    public UUID createMockEndpoint(UUID mockServerId, MockEndpointSettings settings) {
        try {
            var mockServer = collection.findById(mockServerId).get(); // todo: impl 404
            var mockEndpointId = addMockEndpointTo(mockServer, settings);
            mockServer.setUpdateDateTime(LocalDateTime.now());
            collection.save(mockServer);
            storage.putSampleAs(mockEndpointId.toString());
            return mockEndpointId;
        } catch (ScriptStorageException e) {
            throw new MockServiceException("Exception occurred while mock server creating", e);
        }
    }

    private UUID addMockEndpointTo(MockServerDocument mockServer, MockEndpointSettings settings) {
        // todo: check mock endpoint uniqueness

        var mockEndpoint = MockEndpointEmbeddedDocument
            .builder()
            .method(settings.getMethod())
            .path(settings.getPath())
            .completeAndBuild();

        mockServer.getEndpoints().add(mockEndpoint);
        return mockEndpoint.getId();
    }


    public String getSample() {
        try (var scriptStream = storage.getSample()) {
            return IOUtils.toString(scriptStream, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new MockServiceException("Exception occurred while sample getting", e);
        }
    }

    @Transactional(readOnly = true)
    public String getScript(UUID mockServerId, UUID mockEndpointId) {
        // todo: check endpoint existence
        try (var scriptStream = storage.getScript(mockEndpointId.toString())) {
            return IOUtils.toString(scriptStream, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new MockServiceException(
                "Exception occurred while script \"{}\" getting", mockEndpointId, e);
        }
    }

    @Transactional(readOnly = true)
    public void uploadScript(UUID mockServerId, UUID mockEndpointId, String script) {
        // todo: check endpoint existence, script evaluation
        try (var scriptStream = IOUtils.toInputStream(script, StandardCharsets.UTF_8)) {
            storage.putScript(scriptStream, mockEndpointId.toString());
        } catch (Exception e) {
            throw new MockServiceException(
                "Exception occurred while script \"{}\" uploading", mockEndpointId, e);
        }
    }
}
