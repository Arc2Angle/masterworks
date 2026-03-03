package com.masterworks.masterworks.typed.tag;

import java.util.stream.Stream;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;

public abstract class TypedTagKey<T, U extends T> {
    protected final TagKey<T> untyped;
    protected final Registry<T> registry;

    protected TypedTagKey(TagKey<T> untyped, Registry<T> registry) {
        this.untyped = untyped;
        this.registry = registry;
    }

    public TagKey<T> untyped() {
        return untyped;
    }

    public abstract Stream<U> values();
}
