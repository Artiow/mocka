package org.mocka.throwable;

public class ResourceNotFoundException extends NestedRuntimeException {

    public ResourceNotFoundException() {
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceNotFoundException(String format, Object... args) {
        super(format, args);
    }

    public ResourceNotFoundException(String format, Object arg, Throwable cause) {
        super(format, arg, cause);
    }

    public ResourceNotFoundException(String format, Object arg1, Object arg2, Throwable cause) {
        super(format, arg1, arg2, cause);
    }

    public ResourceNotFoundException(String format, Object arg1, Object arg2, Object arg3, Throwable cause) {
        super(format, arg1, arg2, arg3, cause);
    }

    public ResourceNotFoundException(String format, Object[] args, Throwable cause) {
        super(format, args, cause);
    }
}
