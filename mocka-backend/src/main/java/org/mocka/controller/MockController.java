package org.mocka.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.mocka.service.MockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Mock management")
@RestController
@RequiredArgsConstructor
public class MockController {

    private final MockService service;


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
    public ResponseEntity<String> getScript(@PathVariable Integer id) {
        var script = service.getScript(id);
        return ResponseEntity.ok(script);
    }

    @PutMapping(value = "/method/{id}/script", consumes = {"application/javascript", "application/ecmascript", "text/javascript", "text/ecmascript"})
    public ResponseEntity<Void> uploadScript(@PathVariable Integer id, @RequestBody String script) {
        service.uploadScript(id, script);
        return ResponseEntity.noContent().build();
    }


    @GetMapping(value = "/script/sample", produces = "text/javascript")
    public ResponseEntity<String> getSample() {
        var sample = service.getSample();
        return ResponseEntity.ok(sample);
    }
}
