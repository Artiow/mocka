package org.mocka.util;

@FunctionalInterface
public interface ThrowingRunnable {

    void run() throws Exception;
}
