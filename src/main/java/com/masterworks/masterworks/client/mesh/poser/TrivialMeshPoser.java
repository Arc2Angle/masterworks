package com.masterworks.masterworks.client.mesh.poser;

import com.masterworks.masterworks.client.mesh.MeshPoser;
import com.mojang.blaze3d.vertex.PoseStack;

public record TrivialMeshPoser() implements MeshPoser {
    @Override
    public MeshPoser enter(String name, PoseStack stack) {
        return this;
    }
}
