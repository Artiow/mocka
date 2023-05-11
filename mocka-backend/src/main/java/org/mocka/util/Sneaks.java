package org.mocka.util;

import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Sneaks {

    public static <T> Consumer<T> sneakyConsumer(ThrowingConsumer<T> consumer) {
        return new SneakyConsumer<>(consumer);
    }

    public static Runnable sneakyRunnable(ThrowingRunnable runnable) {
        return new SneakyRunnable(runnable);
    }

    public static <T> Supplier<T> sneakySupplier(ThrowingSupplier<T> supplier) {
        return new SneakySupplier<>(supplier);
    }


    @RequiredArgsConstructor
    private static class SneakyConsumer<T> implements Consumer<T> {

        private final ThrowingConsumer<T> consumer;

        @Override @SneakyThrows public void accept(T t) { consumer.accept(t); }
    }

    @RequiredArgsConstructor
    private static class SneakyRunnable implements Runnable {

        private final ThrowingRunnable runnable;

        @Override @SneakyThrows public void run() { runnable.run(); }
    }

    @RequiredArgsConstructor
    private static class SneakySupplier<T> implements Supplier<T> {

        private final ThrowingSupplier<T> supplier;

        @Override @SneakyThrows public T get() { return supplier.get(); }
    }
}
