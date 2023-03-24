package org.mocka.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.mocka.domain.MockServerDocument;
import org.mocka.domain.MockServerDocumentCollection;
import org.mocka.dto.MockServerDto;
import org.mocka.model.MockEndpointSettings;
import org.mocka.storage.ScriptStorage;
import org.mocka.storage.ScriptStorageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MockService {

    private final MockServerDocumentCollection collection;
    private final ScriptStorage storage;


    @Transactional(readOnly = true)
    public MockServerDto getMockServer(String mockServerId) {
        throw new UnsupportedOperationException();
    }

    @Transactional
    public String createMockServer() {
        try {
            var now = LocalDateTime.now();
            var mockServer = MockServerDocument.builder().createDateTime(now).updateDateTime(now).build();
            var mockEndpointId = addMockEndpoint(mockServer, MockEndpointSettings.DEFAULT);
            var mockServerId = collection.insert(mockServer).getId().toString();
            storage.putSampleAs(mockEndpointId);
            return mockServerId;
        } catch (ScriptStorageException e) {
            throw new MockServiceException("Exception occurred while mock server creating", e);
        }
    }


    private String addMockEndpoint(MockServerDocument mockServer, MockEndpointSettings settings) {
        // todo: check mock endpoint uniqueness

        var mockEndpoint = MockServerDocument.MockEndpoint
            .builder()
            .id(ObjectId.get())
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
        try (var scriptStream = IOUtils.toInputStream(script, StandardCharsets.UTF_8)) {
            storage.putScript(scriptStream, mockEndpointId);
        } catch (Exception e) {
            throw new MockServiceException(
                "Exception occurred while script \"{}\" uploading", mockEndpointId, e);
        }
    }
}
