package org.mocka.controller;

import lombok.RequiredArgsConstructor;
import org.mocka.service.EndpointService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@ApiIgnore
@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
public class EndpointController {

    private final EndpointService service;

    @RequestMapping(value = "/endpoint/{domain}/**", method = {GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE})
    public ResponseEntity<Object> handle(
            HttpServletRequest request,
            @PathVariable String domain,
            @RequestParam Map<String, String> params
    ) {
        return ResponseEntity.ok(service.handle(request, domain, params));
    }
}
