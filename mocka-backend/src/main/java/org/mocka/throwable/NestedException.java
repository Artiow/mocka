package org.mocka.throwable;

import static org.mocka.util.Formatter.format;

public abstract class NestedException extends Exception {

    public NestedException() { }

    public NestedException(String message) {
        super(message);
    }

    public NestedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NestedException(String format, Object... args) {
        this(format(format, args));
    }

    public NestedException(String format, Object arg, Throwable cause) {
        this(format(format, arg), cause);
    }

    public NestedException(String format, Object arg1, Object arg2, Throwable cause) {
        this(format(format, arg1, arg2), cause);
    }

    public NestedException(String format, Object arg1, Object arg2, Object arg3, Throwable cause) {
        this(format(format, arg1, arg2, arg3), cause);
    }

    public NestedException(String format, Object[] args, Throwable cause) {
        this(format(format, args), cause);
    }
}
