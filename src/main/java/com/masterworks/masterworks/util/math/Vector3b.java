package com.masterworks.masterworks.util.math;

public record Vector3b(boolean x, boolean y, boolean z) implements Vector3bc {
    public Vector3b() {
        this(false, false, false);
    }

    @Override
    public boolean any() {
        return x || y || z;
    }
}
