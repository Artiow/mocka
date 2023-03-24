package org.mocka.throwable;

import static org.mocka.util.MessageUtils.msg;

public abstract class NestedRuntimeException extends RuntimeException {

    public NestedRuntimeException() {
    }

    public NestedRuntimeException(String message) {
        super(message);
    }

    public NestedRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NestedRuntimeException(String format, Object... args) {
        this(msg(format, args));
    }

    public NestedRuntimeException(String format, Object arg, Throwable cause) {
        this(msg(format, arg), cause);
    }

    public NestedRuntimeException(String format, Object arg1, Object arg2, Throwable cause) {
        this(msg(format, arg1, arg2), cause);
    }

    public NestedRuntimeException(String format, Object arg1, Object arg2, Object arg3, Throwable cause) {
        this(msg(format, arg1, arg2, arg3), cause);
    }

    public NestedRuntimeException(String format, Object[] args, Throwable cause) {
        this(msg(format, args), cause);
    }
}
