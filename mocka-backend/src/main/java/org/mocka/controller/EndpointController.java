package org.mocka.controller;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.springframework.web.bind.annotation.RequestMethod.TRACE;

import java.util.UUID;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.util.RequestUtil;
import org.mocka.service.EndpointService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
public class EndpointController {

    private static final Pattern PREFIX_PATTERN = Pattern.compile("/mock/[^/]*/");

    private final EndpointService service;


    @RequestMapping(
        value = "/mock/{mockServerId}/**",
        method = {GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE})
    public ResponseEntity<Object> handle(@PathVariable UUID mockServerId, HttpServletRequest request) {
        return service.handle(mockServerId, new ForwardedHttpServletRequest(request, PREFIX_PATTERN, "/"));
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
