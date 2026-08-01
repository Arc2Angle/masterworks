package com.masterworks.masterworks.asset;

import com.masterworks.masterworks.client.asset.AssetRegistry;
import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.resources.Identifier;

public record AssetHolder<T>(Identifier id) {
    public static <T> Codec<AssetHolder<T>> codec() {
        return Identifier.CODEC.xmap(AssetHolder::new, AssetHolder::id);
    }

    public Optional<T> value(AssetRegistry<T> registry) {
        return registry.get(id);
    }

    public T valueOrThrow(AssetRegistry<T> registry) {
        return value(registry).orElseThrow(() -> new IllegalArgumentException("Asset not found: " + id));
    }
}
