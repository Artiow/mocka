package org.mocka.storage;

import org.mocka.throwable.NestedException;

public class ScriptStorageException extends NestedException {

    public ScriptStorageException(String message) {
        super(message);
    }

    public ScriptStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScriptStorageException(String format, Object... args) {
        super(format, args);
    }

    public ScriptStorageException(String format, Object arg, Throwable cause) {
        super(format, arg, cause);
    }
}
