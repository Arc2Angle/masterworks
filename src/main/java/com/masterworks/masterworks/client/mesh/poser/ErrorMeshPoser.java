package com.masterworks.masterworks.client.mesh.poser;

import com.masterworks.masterworks.client.mesh.MeshPoser;
import com.mojang.blaze3d.vertex.PoseStack;

public record ErrorMeshPoser(RuntimeException e) implements MeshPoser {
    @Override
    public MeshPoser enter(String name, PoseStack stack) {
        throw e;
    }
}
