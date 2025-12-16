package com.masterworks.masterworks.init.registrar;

import java.util.LinkedList;
import java.util.List;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

public class DataPackRegistriesRegistrar {
    final String namespace;
    final List<Entry<?>> entries = new LinkedList<>();

    public DataPackRegistriesRegistrar(String namespace) {
        this.namespace = namespace;
    }

    public <T> ResourceKey<Registry<T>> registerDataPackRegistry(String name, Codec<T> codec) {
        ResourceKey<Registry<T>> key = ResourceKey
                .createRegistryKey(ResourceLocation.fromNamespaceAndPath(namespace, name));
        entries.add(new Entry<T>(key, codec));
        return key;
    }

    public void register(IEventBus bus) {
        bus.addListener(this::addEntries);
    }

    void addEntries(DataPackRegistryEvent.NewRegistry event) {
        for (var entry : entries) {
            entry.apply(event);
        }
    }

    record Entry<T>(ResourceKey<Registry<T>> key, Codec<T> codec) {
        void apply(DataPackRegistryEvent.NewRegistry event) {
            event.dataPackRegistry(key, codec, codec);
        }
    }
}
