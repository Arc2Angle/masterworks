package com.masterworks.masterworks.location;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public interface RegisteredReferenceLocation<T> extends ReferenceLocation {
    Registry<T> registry();

    public default Holder.Reference<T> registered() {
        Registry<T> registry = registry();
        ResourceLocation key = value();
        return registry.get(key).orElseThrow(() -> new MissingRegistryKeyException(key, registry));
    }

    public static class MissingRegistryKeyException extends RuntimeException {
        public MissingRegistryKeyException(ResourceLocation key, Registry<?> registry) {
            super("Missing " + key + " in registry " + registry);
        }
    }
}
