package com.masterworks.masterworks.util;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;

/**
 * @param <R> The key's erased reference type
 * @param <K> The key generic concrete type
 * @param <V> The value type
 */
public class ReferenceDispatchedMap<R, K, V> {
    public static <R, K, V> Codec<ReferenceDispatchedMap<R, K, V>> codec(Codec<R> referenceCodec,
            Function<? super R, ? extends K> toKey,
            Function<? super K, ? extends Decoder<? extends V>> toParser) {
        return Codec.unboundedMap(referenceCodec, Codec.PASSTHROUGH).xmap(
                dynamics -> new ReferenceDispatchedMap<>(dynamics, toKey, toParser),
                instance -> instance.dynamics);
    }

    final Map<R, Dynamic<?>> dynamics;
    final Map<K, ? extends V> values;

    ReferenceDispatchedMap(Map<R, Dynamic<?>> dynamics, Function<? super R, ? extends K> toKey,
            Function<? super K, ? extends Decoder<? extends V>> toDecoder) {
        this.dynamics = dynamics;
        this.values = dynamics.entrySet().stream().map(entry -> {
            R reference = entry.getKey();
            Dynamic<?> dynamic = entry.getValue();

            K key = toKey.apply(reference);
            V value = toDecoder.apply(key).parse(dynamic).getOrThrow();

            return Map.entry(key, value);
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Optional<V> get(K key) {
        return Optional.ofNullable(values.get(key));
    }
}
