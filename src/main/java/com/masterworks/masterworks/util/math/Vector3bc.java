package com.masterworks.masterworks.util.math;

public interface Vector3bc {
    boolean x();

    boolean y();

    boolean z();

    default boolean any() {
        return x() || y() || z();
    }
}
