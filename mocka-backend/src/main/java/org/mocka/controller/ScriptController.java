package org.mocka.controller;

import org.mocka.service.ScriptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ScriptController {

    private final ScriptService service;


    @GetMapping(value = "/method/{id}")
    public ResponseEntity<Void> get(@PathVariable Integer id) {
        return ResponseEntity.noContent().build(); // todo
    }

    @PostMapping(value = "/method", consumes = {"application/json"})
    public ResponseEntity<Void> create(@RequestBody Object payload) {
        return ResponseEntity.noContent().build(); // todo
    }

    @PutMapping(value = "/method/{id}", consumes = {"application/json"})
    public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody Object payload) {
        return ResponseEntity.noContent().build(); // todo
    }

    @DeleteMapping(value = "/method/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        return ResponseEntity.noContent().build(); // todo
    }


    @GetMapping(value = "/method/{id}/script")
    public ResponseEntity<Void> getScript(@PathVariable Integer id) {
        return ResponseEntity.noContent().build(); // todo
    }

    @PutMapping(value = "/method/{id}/script", consumes = {"application/javascript", "application/ecmascript", "text/javascript", "text/ecmascript"})
    public ResponseEntity<Void> upload(@PathVariable Integer id, @RequestBody String script) {
        service.upload(id, script);
        return ResponseEntity.noContent().build();
    }
}
