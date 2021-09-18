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
@RestController
@RequiredArgsConstructor
public class EndpointController {

    private final EndpointService service;


    @RequestMapping(value = "/endpoint/{subdomain}/**", method = {GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE})
    public ResponseEntity<Object> handle(
            HttpServletRequest request,
            @PathVariable String subdomain,
            @RequestBody(required = false) String body,
            @RequestParam Map<String, String> params
    ) {
        return service.handle(request, subdomain, body, params);
    }
}
