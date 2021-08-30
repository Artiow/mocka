package org.mocka.service;

import org.mocka.runner.ScriptRunner;
import org.mocka.runner.ScriptRunnerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EndpointService {

    private final ScriptRunner scriptRunner;


    public Object handle(HttpServletRequest request, String domain, Map<String, String> params) {
        try {
            return scriptRunner.invoke(params);
        } catch (ScriptRunnerException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
