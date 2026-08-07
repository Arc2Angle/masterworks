package com.masterworks.masterworks.client.mesh;

import com.mojang.blaze3d.vertex.PoseStack;

/**
 * Poses a {@code PoseStack} within a namespace.
 */
public interface MeshPoser {
    MeshPoser enter(String name, PoseStack stack);
}
