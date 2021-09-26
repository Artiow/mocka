package org.mocka.controller;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.util.RequestUtil;
import org.mocka.service.EndpointService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Map;
import java.util.regex.Pattern;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@ApiIgnore
@CrossOrigin("*")
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
        var forwardedRequest = new ForwardedHttpServletRequest(request, Pattern.compile("/endpoint/[^/]*/"), "/");
        return service.handle(forwardedRequest, subdomain, body, params);
    }


    private static class ForwardedHttpServletRequest extends HttpServletRequestWrapper {

        private final String requestUri;

        public ForwardedHttpServletRequest(HttpServletRequest request, Pattern pattern, String replacement) {
            super(request);
            this.requestUri = pattern.matcher(request.getRequestURI()).replaceFirst(replacement);
        }

        @Override
        public String getRequestURI() {
            return this.requestUri;
        }

        @Override
        public StringBuffer getRequestURL() {
            return RequestUtil.getRequestURL(this);
        }
    }
}
