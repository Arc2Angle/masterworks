package com.masterworks.masterworks.typed.identifier;

import com.masterworks.masterworks.client.asset.manager.AssetManager;
import java.util.Optional;

public interface AssetIdentifier<T> extends TypedIdentifier {
    default Optional<T> asset(AssetManager<T> manager) {
        return manager.get(id());
    }

    default T assetOrThrow(AssetManager<T> manager) {
        return manager.get(id()).orElseThrow(() -> new IllegalStateException("Missing asset \"" + id() + "\""));
    }
}
