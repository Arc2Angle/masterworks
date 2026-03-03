package com.masterworks.masterworks.util.registrar;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

public class DataMapTypesRegistrar {
    final String namespace;
    final List<DataMapType<?, ?>> entries = new LinkedList<>();

    public DataMapTypesRegistrar(String namespace) {
        this.namespace = namespace;
    }

    public <K, V> DataMapType<K, V> registerDataMapType(String name, Function<Identifier, DataMapType<K, V>> factory) {
        Identifier id = Identifier.fromNamespaceAndPath(namespace, name);
        DataMapType<K, V> dataMapType = factory.apply(id);
        entries.add(dataMapType);
        return dataMapType;
    }

    public void register(IEventBus bus) {
        bus.addListener(this::addEntries);
    }

    void addEntries(RegisterDataMapTypesEvent event) {
        for (var entry : entries) {
            event.register(entry);
        }
    }
}
