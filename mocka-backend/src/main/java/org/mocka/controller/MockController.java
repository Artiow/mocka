package org.mocka.controller;

import io.swagger.annotations.Api;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.mocka.service.MockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@Api(tags = "Mock management")
@RestController
@RequiredArgsConstructor
public class MockController {

    private final MockService service;


    @PostMapping(value = "/server")
    public ResponseEntity<Void> createMockServer(HttpServletRequest request) {
        var mockServerId = service.createMockServer();
        return ResponseEntity
            .created(UriComponentsBuilder
                .fromUriString(request.getRequestURI())
                .path("/{mockServerId}")
                .build(mockServerId))
            .build();
    }

    @GetMapping(value = "/method/{id}")
    public ResponseEntity<Void> get(@PathVariable Integer id) {
        return ResponseEntity.noContent().build(); // todo
    }

    @PostMapping(value = "/method", consumes = "application/json")
    public ResponseEntity<Void> create(@RequestBody Object payload) {
        return ResponseEntity.noContent().build(); // todo
    }

    @PutMapping(value = "/method/{id}", consumes = "application/json")
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody Object payload) {
        return ResponseEntity.noContent().build(); // todo
    }

    @DeleteMapping(value = "/method/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        return ResponseEntity.noContent().build(); // todo
    }


    @GetMapping(value = "/method/{id}/script", produces = "text/javascript")
    public ResponseEntity<String> getScript(@PathVariable String id) {
        var script = service.getScript(id);
        return ResponseEntity.ok(script);
    }

    @PutMapping(value = "/method/{id}/script", consumes = {"application/javascript", "application/ecmascript", "text/javascript", "text/ecmascript"})
    public ResponseEntity<Void> uploadScript(@PathVariable String id, @RequestBody String script) {
        service.uploadScript(id, script);
        return ResponseEntity.noContent().build();
    }


    @GetMapping(value = "/script/sample", produces = "text/javascript")
    public ResponseEntity<String> getSample() {
        var sample = service.getSample();
        return ResponseEntity.ok(sample);
    }
}
