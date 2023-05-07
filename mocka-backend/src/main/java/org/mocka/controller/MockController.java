package org.mocka.controller;

import io.swagger.annotations.Api;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.mocka.model.MockEndpointSettings;
import org.mocka.service.MockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Api(tags = "Mock management")
@RestController
@RequiredArgsConstructor
public class MockController {

    private final MockService service;


    @GetMapping(value = "/server/{mockServerId}")
    public ResponseEntity<Object> getMockServer(
        @PathVariable UUID mockServerId) {
        return ResponseEntity.ok(null); // todo
    }

    @PostMapping(value = "/server")
    public ResponseEntity<Void> createMockServer(
        @RequestParam(required = false, defaultValue = "false") boolean stub) {
        var mockServerId = service.createMockServer(stub);
        return ResponseEntity
            .created(ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{mockServerId}")
                .build(mockServerId))
            .build();
    }

    @DeleteMapping(value = "/server/{mockServerId}")
    public ResponseEntity<Void> deleteMockServer(
        @PathVariable UUID mockServerId) {
        return ResponseEntity.noContent().build(); // todo
    }


    @GetMapping(value = "/server/{mockServerId}/method/{mockEndpointId}")
    public ResponseEntity<Object> getMockEndpoint(
        @PathVariable UUID mockServerId,
        @PathVariable UUID mockEndpointId) {
        return ResponseEntity.ok(null); // todo
    }

    @PostMapping(value = "/server/{mockServerId}/method")
    public ResponseEntity<Void> createMockEndpoint(
        @PathVariable UUID mockServerId,
        @RequestBody MockEndpointSettings settings) {
        var mockEndpointId = service.createMockEndpoint(mockServerId, settings);
        return ResponseEntity
            .created(ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{mockEndpointId}")
                .build(mockEndpointId))
            .build();
    }

    @DeleteMapping(value = "/server/{mockServerId}/method/{mockEndpointId}")
    public ResponseEntity<Void> deleteMockEndpoint(
        @PathVariable UUID mockServerId,
        @PathVariable UUID mockEndpointId) {
        return ResponseEntity.noContent().build(); // todo
    }


    @GetMapping(
        value = "/server/{mockServerId}/method/{mockEndpointId}/script",
        produces = "text/javascript")
    public ResponseEntity<String> getScript(
        @PathVariable UUID mockServerId,
        @PathVariable UUID mockEndpointId) {
        var script = service.getScript(mockServerId, mockEndpointId);
        return ResponseEntity.ok(script);
    }

    @PutMapping(
        value = "/server/{mockServerId}/method/{mockEndpointId}/script",
        consumes = {"text/javascript", "text/ecmascript", "application/javascript", "application/ecmascript"})
    public ResponseEntity<Void> putScript(
        @PathVariable UUID mockServerId,
        @PathVariable UUID mockEndpointId,
        @RequestBody String script) {
        service.putScript(mockServerId, mockEndpointId, script);
        return ResponseEntity.noContent().build();
    }
}
