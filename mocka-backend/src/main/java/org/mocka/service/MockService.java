package org.mocka.service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.mocka.domain.entity.MockEndpointEntity;
import org.mocka.domain.entity.MockServerEntity;
import org.mocka.domain.repository.MockServerRepository;
import org.mocka.model.MockEndpointSettings;
import org.mocka.storage.ScriptStorage;
import org.mocka.storage.ScriptStorageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MockService {

    private final MockServerRepository repository;
    private final ScriptStorage storage;


    @Transactional
    public String createMockServer() {
        try {
            var now = LocalDateTime.now();
            var mockServer = MockServerEntity.builder().createDateTime(now).updateDateTime(now).build();
            var mockEndpointId = addMockEndpoint(mockServer, MockEndpointSettings.DEFAULT);
            var mockServerId = repository.insert(mockServer).getId().toString();
            storage.putSampleAs(mockEndpointId);
            return mockServerId;
        } catch (ScriptStorageException e) {
            throw new MockServiceException("Exception occurred mock server creating", e);
        }
    }


    private String addMockEndpoint(MockServerEntity mockServer, MockEndpointSettings settings) {
        var mockEndpoint = MockEndpointEntity
            .builder()
            .method(settings.getMethod().toString())
            .path(settings.getPath())
            .build();

        mockServer.getEndpoints().add(mockEndpoint);
        return mockEndpoint.getId().toString();
    }


    public String getSample() {
        try (var scriptStream = storage.getSample()) {
            return IOUtils.toString(scriptStream, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new MockServiceException("Exception occurred while sample getting", e);
        }
    }

    public String getScript(String mockEndpointId) {
        // todo: check endpoint existence
        try (var scriptStream = storage.getScript(mockEndpointId)) {
            return IOUtils.toString(scriptStream, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new MockServiceException(
                "Exception occurred while script \"{}\" getting", mockEndpointId, e);
        }
    }

    public void uploadScript(String mockEndpointId, String script) {
        // todo: check endpoint existence, script evaluation
        try (var scriptStream = new ByteArrayInputStream(script.getBytes(StandardCharsets.UTF_8))) {
            storage.putScript(scriptStream, mockEndpointId);
        } catch (Exception e) {
            throw new MockServiceException(
                "Exception occurred while script \"{}\" uploading", mockEndpointId, e);
        }
    }
}
