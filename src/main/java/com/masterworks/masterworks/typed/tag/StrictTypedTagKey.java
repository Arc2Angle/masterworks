package com.masterworks.masterworks.typed.tag;

import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;

public abstract class StrictTypedTagKey<T, U extends T> extends TypedTagKey<T, U> {

    protected StrictTypedTagKey(TagKey<T> untyped, Registry<T> registry) {
        super(untyped, registry);
    }

    protected abstract U cast(T value);

    @Override
    public Stream<U> values() {
        return registry.getOrThrow(untyped).stream().map(holder -> cast(holder.value()));
    }

    public static <T, U extends T> TypedTagKey<T, U> create(
            TagKey<T> untyped, Registry<T> registry, Function<? super T, ? extends U> cast) {
        return new StrictTypedTagKey<T, U>(untyped, registry) {
            @Override
            protected U cast(T value) {
                return cast.apply(value);
            }
        };
    }
}
