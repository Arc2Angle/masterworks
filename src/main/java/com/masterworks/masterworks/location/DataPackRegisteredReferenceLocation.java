package com.masterworks.masterworks.location;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public interface DataPackRegisteredReferenceLocation<T> extends RegisteredReferenceLocation<T> {
    ResourceKey<? extends Registry<T>> registryKey();

    public default Registry<T> registry() {
        RegistryAccess registryAccess = ServerLifecycleHooks.getCurrentServer().registryAccess();
        ResourceKey<? extends Registry<T>> key = registryKey();
        return registryAccess.lookup(key).orElseThrow(() -> new MissingRegistryException(key));
    }

    public static class MissingRegistryException extends RuntimeException {
        public MissingRegistryException(ResourceKey<? extends Registry<?>> registryKey) {
            super("Missing " + registryKey + " registry ");
        }
    }
}
