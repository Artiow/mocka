package org.mocka.runner;

import lombok.Getter;

@Getter
public class InvokeException extends Exception {

    private final Type errorType;


    public InvokeException(Type errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    public InvokeException(Type errorType, String message, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }

    public InvokeException(String message, Throwable cause) {
        this(Type.SERVICE_EXCEPTION, message, cause);
    }


    public enum Type {

        RESULT_IS_NULL,
        RESULT_IS_NOT_OBJECT,
        SERVICE_EXCEPTION
    }
}
