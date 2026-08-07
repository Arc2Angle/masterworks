package com.masterworks.masterworks.client;

import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.MasterworksRegistries;
import com.masterworks.masterworks.client.mesh.MeshDefinition;
import com.masterworks.masterworks.client.mesh.wavefront.WavefrontMeshDefinition;
import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MasterworksMeshDefinitionTypes {
    private static final DeferredRegister<MeshDefinition.Type<?>> REGISTRAR =
            DeferredRegister.create(MasterworksRegistries.MESH_DEFINITION_TYPE, MasterworksMod.ID);

    private static <T extends MeshDefinition> Supplier<MeshDefinition.Type<T>> register(
            String name, MapCodec<T> codec) {
        return REGISTRAR.register(name, () -> new MeshDefinition.Type<>(codec));
    }

    public static final Supplier<MeshDefinition.Type<WavefrontMeshDefinition>> WAVEFRONT =
            register("wavefront", WavefrontMeshDefinition.CODEC);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }
}
