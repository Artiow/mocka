package org.mocka.runner.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScriptResponse {

    private int status;
    private Object body;
}
