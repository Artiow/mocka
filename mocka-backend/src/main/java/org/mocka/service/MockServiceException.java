package org.mocka.service;

import org.mocka.throwable.InternalServerErrorException;

public class MockServiceException extends InternalServerErrorException {

    public MockServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public MockServiceException(String format, Object arg, Throwable cause) {
        super(format, arg, cause);
    }
}
