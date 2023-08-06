package org.mocka.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings({"Convert2Lambda", "Anonymous2MethodRef"})
public class Sneaks {

    public static Runnable sneakyRunnable(ThrowingRunnable runnable) {
        return new Runnable() { @Override @SneakyThrows public void run() { runnable.run(); } };
    }

    public static <T> Supplier<T> sneakySupplier(ThrowingSupplier<T> supplier) {
        return new Supplier<>() { @Override @SneakyThrows public T get() { return supplier.get(); } };
    }

    public static <T> Consumer<T> sneakyConsumer(ThrowingConsumer<T> consumer) {
        return new Consumer<>() { @Override @SneakyThrows public void accept(T t) { consumer.accept(t); } };
    }

    public static <T, R> Function<T, R> sneakyFunction(ThrowingFunction<T, R> function) {
        return new Function<>() { @Override @SneakyThrows public R apply(T t) { return function.apply(t); } };
    }
}
