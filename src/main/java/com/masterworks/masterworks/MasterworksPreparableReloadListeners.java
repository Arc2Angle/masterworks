package com.masterworks.masterworks;

import com.masterworks.masterworks.client.asset.manager.PaletteManager;
import com.masterworks.masterworks.client.asset.manager.VoxFileManager;
import com.masterworks.masterworks.util.registrar.PreparableReloadListenersRegistrar;
import java.util.function.Supplier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.bus.api.IEventBus;

public class MasterworksPreparableReloadListeners {
    private static final PreparableReloadListenersRegistrar REGISTRAR =
            new PreparableReloadListenersRegistrar(MasterworksMod.ID);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    private static <T extends PreparableReloadListener> Supplier<T> register(String path, Supplier<T> factory) {
        return REGISTRAR.registerPreparableReloadListener(path, factory);
    }

    public static final Supplier<VoxFileManager> VOX_FILE_MANAGER = register("voxels", VoxFileManager::new);

    public static final Supplier<PaletteManager> PALETTE_MANAGER = register("textures/palette", PaletteManager::new);
}
