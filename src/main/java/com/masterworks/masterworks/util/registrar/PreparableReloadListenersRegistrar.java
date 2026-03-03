package com.masterworks.masterworks.util.registrar;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;

public class PreparableReloadListenersRegistrar {
    final String namespace;
    final List<Entry<?>> entries = new LinkedList<>();

    public PreparableReloadListenersRegistrar(String namespace) {
        this.namespace = namespace;
    }

    public <T extends PreparableReloadListener> Supplier<T> registerPreparableReloadListener(
            String path, Supplier<T> factory) {
        Entry<T> entry = new Entry<>(Identifier.fromNamespaceAndPath(namespace, path), factory);
        entries.add(entry);
        return entry;
    }

    public void register(IEventBus bus) {
        bus.addListener(this::addEntries);
    }

    void addEntries(AddClientReloadListenersEvent event) {
        for (var entry : entries) {
            entry.apply(event);
        }
    }

    class Entry<T extends PreparableReloadListener> implements Supplier<T> {
        final Identifier id;
        final Supplier<T> factory;

        T value = null;

        Entry(Identifier id, Supplier<T> factory) {
            this.id = id;
            this.factory = factory;
        }

        public T get() {
            if (value == null) {
                value = factory.get();
            }
            return value;
        }

        public void apply(AddClientReloadListenersEvent event) {
            event.addListener(id, get());
        }
    }
}
