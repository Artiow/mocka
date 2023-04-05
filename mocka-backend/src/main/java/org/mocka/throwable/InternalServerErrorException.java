package org.mocka.throwable;

public class InternalServerErrorException extends NestedRuntimeException {

    public InternalServerErrorException() { }

    public InternalServerErrorException(String message) {
        super(message);
    }

    public InternalServerErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalServerErrorException(String format, Object... args) {
        super(format, args);
    }

    public InternalServerErrorException(String format, Object arg, Throwable cause) {
        super(format, arg, cause);
    }

    public InternalServerErrorException(String format, Object arg1, Object arg2, Throwable cause) {
        super(format, arg1, arg2, cause);
    }

    public InternalServerErrorException(String format, Object arg1, Object arg2, Object arg3, Throwable cause) {
        super(format, arg1, arg2, arg3, cause);
    }

    public InternalServerErrorException(String format, Object[] args, Throwable cause) {
        super(format, args, cause);
    }
}
