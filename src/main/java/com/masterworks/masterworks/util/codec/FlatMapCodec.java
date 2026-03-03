package com.masterworks.masterworks.util.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public final class FlatMapCodec<K, V> extends MapCodec<Map<K, V>> {
    private final Codec<K> keyCodec;
    private final Codec<V> valueCodec;
    private final Set<K> ignoredKeys;

    public FlatMapCodec(Codec<K> keyCodec, Codec<V> valueCodec, Set<K> ignoredKeys) {
        this.keyCodec = keyCodec;
        this.valueCodec = valueCodec;
        this.ignoredKeys = ignoredKeys;
    }

    @Override
    public <T> Stream<T> keys(DynamicOps<T> ops) {
        return Stream.empty();
    }

    @Override
    public <T> DataResult<Map<K, V>> decode(DynamicOps<T> ops, MapLike<T> input) {
        DataResult<Map<K, V>> result = DataResult.success(new HashMap<>());

        for (Pair<T, T> pair : input.entries().toList()) {
            DataResult<K> keyResult = keyCodec.parse(ops, pair.getFirst());

            if (keyResult.result().map(ignoredKeys::contains).orElse(false)) {
                continue;
            }

            DataResult<Map.Entry<K, V>> entryResult = keyResult.flatMap(key -> valueCodec
                    .parse(ops, pair.getSecond())
                    .mapError(err -> "Failed to parse value for key '" + key + "': " + err)
                    .map(val -> Map.entry(key, val)));

            result = result.apply2(
                    (map, entry) -> {
                        map.put(entry.getKey(), entry.getValue());
                        return map;
                    },
                    entryResult);
        }

        return result;
    }

    @Override
    public <T> RecordBuilder<T> encode(Map<K, V> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
        for (Map.Entry<K, V> entry : input.entrySet()) {
            prefix.add(keyCodec.encodeStart(ops, entry.getKey()), valueCodec.encodeStart(ops, entry.getValue()));
        }
        return prefix;
    }

    public static <K, V> FlatMapCodec<K, V> forDispatchCodec(Codec<K> keyCodec, Codec<V> valueCodec, K dispatchKey) {
        return new FlatMapCodec<>(keyCodec, valueCodec, Set.of(dispatchKey));
    }
}
