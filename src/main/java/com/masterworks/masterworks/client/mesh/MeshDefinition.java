package com.masterworks.masterworks.client.mesh;

import com.masterworks.masterworks.MasterworksRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.packs.resources.ResourceManager;

public interface MeshDefinition {
    public static final Codec<MeshDefinition> CODEC =
            MasterworksRegistries.MESH_DEFINITION_TYPE.byNameCodec().dispatch(MeshDefinition::type, Type::codec);

    Type<?> type();

    Mesh bake(ResourceManager resourceManager);

    record Type<T extends MeshDefinition>(MapCodec<T> codec) {}
}
