package com.masterworks.masterworks.client.mesh.empty;

import com.masterworks.masterworks.client.MasterworksMeshDefinitionTypes;
import com.masterworks.masterworks.client.mesh.Mesh;
import com.masterworks.masterworks.client.mesh.MeshDefinition;
import com.masterworks.masterworks.client.mesh.core.EmptyMesh;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.packs.resources.ResourceManager;

public record EmptyMeshDefinition() implements MeshDefinition {
    public static final MapCodec<EmptyMeshDefinition> CODEC = MapCodec.unit(EmptyMeshDefinition::new);

    @Override
    public Type<?> type() {
        return MasterworksMeshDefinitionTypes.EMPTY.get();
    }

    @Override
    public Mesh bake(ResourceManager resourceManager) {
        return new EmptyMesh();
    }
}
