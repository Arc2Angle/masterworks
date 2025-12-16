package com.masterworks.masterworks.init.registrar;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.NewRegistryEvent;

public class RegistriesRegistrar {
    final String namespace;
    final List<Registry<?>> entries = new LinkedList<>();

    public RegistriesRegistrar(String namespace) {
        this.namespace = namespace;
    }

    public <T> Registry<T> registerRegistry(String name,
            Function<ResourceKey<Registry<T>>, Registry<T>> factory) {
        ResourceKey<Registry<T>> key = ResourceKey
                .createRegistryKey(ResourceLocation.fromNamespaceAndPath(namespace, name));
        Registry<T> registry = factory.apply(key);
        entries.add(registry);
        return registry;
    }

    public void register(IEventBus bus) {
        bus.addListener(this::addEntries);
    }

    void addEntries(NewRegistryEvent event) {
        for (var entry : entries) {
            event.register(entry);
        }
    }
}
