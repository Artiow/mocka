package org.mocka.util;

import java.util.function.Supplier;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Suppliers {

    public static <T> Supplier<T> sneaky(ThrowingSupplier<T> supplier) {
        return new SneakySupplier<>(supplier);
    }


    private static class SneakySupplier<T> extends AbstractSafeSupplier<T> {

        private SneakySupplier(ThrowingSupplier<T> supplier) { super(supplier); }


        @Override @SneakyThrows protected T handleException(Exception e) { throw e; }
    }

    private static abstract class AbstractSafeSupplier<T> implements Supplier<T> {

        private final ThrowingSupplier<T> supplier;

        private AbstractSafeSupplier(ThrowingSupplier<T> supplier) { this.supplier = supplier; }


        @Override public T get() { try { return supplier.get(); } catch (Exception e) { return handleException(e); }}

        protected abstract T handleException(Exception e);
    }
}
