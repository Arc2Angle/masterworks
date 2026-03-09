package com.masterworks.masterworks.client;

import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.client.asset.manager.PaletteManager;
import com.masterworks.masterworks.client.asset.manager.VoxFileManager;
import com.masterworks.masterworks.util.registrar.ReloadListenersRegistrar;
import java.util.function.Supplier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.bus.api.IEventBus;

public class MasterworksReloadListeners {
    private static final ReloadListenersRegistrar REGISTRAR = new ReloadListenersRegistrar(MasterworksMod.ID);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    private static <T extends PreparableReloadListener> Supplier<T> register(
            String path, ReloadListenersRegistrar.Type<T> type) {
        return REGISTRAR.registerReloadListener(path, type);
    }

    public static final Supplier<VoxFileManager> VOX_FILE_MANAGER = register("voxels", new VoxFileManager.Type());

    public static final Supplier<PaletteManager> PALETTE_MANAGER =
            register("textures/palette", new PaletteManager.Type());
}
