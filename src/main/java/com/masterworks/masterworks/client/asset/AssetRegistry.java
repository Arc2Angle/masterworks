package com.masterworks.masterworks.client.asset;

import java.util.Optional;
import net.minecraft.resources.Identifier;

public interface AssetRegistry<T> {
    Optional<T> get(Identifier id);
}
