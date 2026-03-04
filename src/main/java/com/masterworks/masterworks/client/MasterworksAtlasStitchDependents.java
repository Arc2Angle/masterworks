package com.masterworks.masterworks.client;

import com.masterworks.masterworks.client.baker.VoxelsBaker;
import com.masterworks.masterworks.util.registrar.AtlasStitchDependenetsRegistrar;
import java.util.function.Supplier;
import net.neoforged.bus.api.IEventBus;

public class MasterworksAtlasStitchDependents {
    private static final AtlasStitchDependenetsRegistrar REGISTRAR = new AtlasStitchDependenetsRegistrar();

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    public static final Supplier<VoxelsBaker> VOXELS_BAKER =
            REGISTRAR.registerAtlasStitchListener(new VoxelsBaker.Factory());
}
