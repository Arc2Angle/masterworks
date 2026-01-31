package com.masterworks.masterworks.util.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @param <R> The key's erased reference type
 * @param <K> The key generic concrete type
 * @param <V> The value type
 */
public class KeyDispatchedMap<R, K, V> {
    protected final Map<R, Dynamic<?>> dynamics;
    protected final Map<K, ? extends V> values;

    protected KeyDispatchedMap(
            Map<R, Dynamic<?>> dynamics,
            Function<? super R, ? extends K> toKey,
            Function<? super K, ? extends Decoder<? extends V>> toDecoder) {
        this.dynamics = dynamics;
        this.values = dynamics.entrySet().stream()
                .map(entry -> {
                    R reference = entry.getKey();
                    Dynamic<?> dynamic = entry.getValue();

                    K key = toKey.apply(reference);
                    V value = toDecoder.apply(key).parse(dynamic).getOrThrow();

                    return Map.entry(key, value);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Optional<V> get(K key) {
        return Optional.ofNullable(values.get(key));
    }

    public static <R, K, V> Codec<KeyDispatchedMap<R, K, V>> codec(
            Codec<R> referenceCodec,
            Function<? super R, ? extends K> toKey,
            Function<? super K, ? extends Decoder<? extends V>> toParser) {
        return Codec.unboundedMap(referenceCodec, Codec.PASSTHROUGH)
                .xmap(dynamics -> new KeyDispatchedMap<>(dynamics, toKey, toParser), instance -> instance.dynamics);
    }
}
