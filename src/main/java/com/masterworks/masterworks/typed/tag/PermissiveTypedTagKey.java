package com.masterworks.masterworks.typed.tag;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;

public abstract class PermissiveTypedTagKey<T, U extends T> extends TypedTagKey<T, U> {

    protected PermissiveTypedTagKey(TagKey<T> untyped, Registry<T> registry) {
        super(untyped, registry);
    }

    protected abstract Optional<? extends U> cast(T value);

    @Override
    public Stream<U> values() {
        return registry.getOrThrow(untyped).stream()
                .map(holder -> cast(holder.value()))
                .flatMap(Optional::stream);
    }

    public static <T, U extends T> TypedTagKey<T, U> create(
            TagKey<T> untyped, Registry<T> registry, Function<? super T, Optional<? extends U>> cast) {
        return new PermissiveTypedTagKey<T, U>(untyped, registry) {
            @Override
            protected Optional<? extends U> cast(T value) {
                return cast.apply(value);
            }
        };
    }

    public static <T, U extends T> TypedTagKey<T, U> createSuppressing(
            TagKey<T> untyped,
            Registry<T> registry,
            Function<? super T, ? extends U> cast,
            Consumer<ClassCastException> suppress) {
        return new PermissiveTypedTagKey<T, U>(untyped, registry) {
            @Override
            protected Optional<? extends U> cast(T value) {
                try {
                    return Optional.of(cast.apply(value));
                } catch (ClassCastException e) {
                    suppress.accept(e);
                    return Optional.empty();
                }
            }
        };
    }
}
