package com.masterworks.masterworks.util;

import java.util.function.Function;
import java.util.function.Supplier;

public class Exceptions {
    @FunctionalInterface
    public interface SupplierWithException<T, X extends Throwable> {
        T get() throws X;
    }

    public static <T, E extends RuntimeException, X extends Throwable> T check(
            Supplier<T> supplier, Class<E> clazz, Function<E, X> mapper) throws X {
        try {
            return supplier.get();
        } catch (RuntimeException e) {
            if (clazz.isInstance(e)) {
                throw mapper.apply(clazz.cast(e));
            } else {
                throw e;
            }
        }
    }

    public static <T, X extends Throwable, E extends RuntimeException> T uncheck(
            SupplierWithException<T, X> supplier, Class<X> clazz, Function<X, E> mapper) {
        try {
            return supplier.get();
        } catch (Throwable e) {
            if (clazz.isInstance(e)) {
                throw mapper.apply(clazz.cast(e));
            } else {
                throw sneaky(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <X extends Throwable> RuntimeException sneaky(Throwable e) throws X {
        throw (X) e;
    }
}
