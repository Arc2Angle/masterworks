package com.masterworks.masterworks.client;

import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.client.asset.manager.PaletteManager;
import com.masterworks.masterworks.client.asset.manager.VoxFileManager;
import com.masterworks.masterworks.util.Registrar;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;

public class MasterworksReloadListeners {
    private static final Registrar<AddClientReloadListenersEvent> REGISTRAR =
            new Registrar<>(MasterworksMod.ID, AddClientReloadListenersEvent.class);

    public interface Type<T extends PreparableReloadListener> {
        default List<Identifier> dependencies() {
            return List.of();
        }

        default List<Identifier> dependents() {
            return List.of();
        }

        T create();
    }

    private static <T extends PreparableReloadListener> Supplier<T> register(String path, Type<T> type) {
        return REGISTRAR.addIdentified(path, id -> REGISTRAR.new Deferred<T>() {
            @Override
            protected T create(AddClientReloadListenersEvent event) {
                T value = type.create();
                event.addListener(id, value);
                type.dependencies().forEach(dependency -> event.addDependency(dependency, id));
                type.dependents().forEach(dependent -> event.addDependency(id, dependent));
                return value;
            }
        });
    }

    public static final Supplier<VoxFileManager> VOX_FILE_MANAGER = register("voxels", new VoxFileManager.Type());

    public static final Supplier<PaletteManager> PALETTE_MANAGER =
            register("textures/palette", new PaletteManager.Type());

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }
}
