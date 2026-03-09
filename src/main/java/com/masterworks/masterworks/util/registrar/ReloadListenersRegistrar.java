package com.masterworks.masterworks.util.registrar;

import com.google.common.base.Suppliers;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;

public class ReloadListenersRegistrar {
    private final String namespace;
    private final List<Entry<?>> entries = new LinkedList<>();

    public ReloadListenersRegistrar(String namespace) {
        this.namespace = namespace;
    }

    public <T extends PreparableReloadListener> Supplier<T> registerReloadListener(String path, Type<T> type) {
        Entry<T> entry = new Entry<>(
                Identifier.fromNamespaceAndPath(namespace, path),
                type.dependencies(),
                type.dependents(),
                Suppliers.memoize(type::create));
        entries.add(entry);
        return entry.value();
    }

    public void register(IEventBus bus) {
        bus.addListener(this::addEntries);
    }

    private void addEntries(AddClientReloadListenersEvent event) {
        for (var entry : entries) {
            entry.apply(event);
        }
    }

    public static interface Type<T extends PreparableReloadListener> {
        default List<Identifier> dependencies() {
            return List.of();
        }

        default List<Identifier> dependents() {
            return List.of();
        }

        T create();
    }

    private record Entry<T extends PreparableReloadListener>(
            Identifier id, List<Identifier> dependencies, List<Identifier> dependents, Supplier<T> value) {
        public void apply(AddClientReloadListenersEvent event) {
            event.addListener(id, value.get());
            dependencies.forEach(dependency -> event.addDependency(dependency, id));
            dependents.forEach(dependent -> event.addDependency(id, dependent));
        }
    }
}
