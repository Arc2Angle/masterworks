package com.masterworks.masterworks.client;

import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.client.mesh.MeshManager;
import com.masterworks.masterworks.util.Registrar;
import java.util.List;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;

public class MasterworksPreparableReloadListeners {
    private static final Registrar<AddClientReloadListenersEvent> REGISTRAR =
            new Registrar<>(MasterworksMod.ID, AddClientReloadListenersEvent.class);

    private static <T extends PreparableReloadListener> T register(
            String path, T listener, List<Identifier> dependencies, List<Identifier> dependents) {
        return REGISTRAR
                .addIdentified(path, id -> REGISTRAR.new Wrapper<T>() {
                    @Override
                    public void accept(AddClientReloadListenersEvent event) {
                        event.addListener(id, listener);
                        dependencies.forEach(dependency -> event.addDependency(dependency, id));
                        dependents.forEach(dependent -> event.addDependency(id, dependent));
                    }

                    @Override
                    public T unwrap() {
                        return listener;
                    }
                })
                .unwrap();
    }

    private static <T extends PreparableReloadListener> T register(String path, T listener) {
        return register(path, listener, List.of(), List.of());
    }

    public static final MeshManager MESH = register("mesh", new MeshManager());

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }
}
