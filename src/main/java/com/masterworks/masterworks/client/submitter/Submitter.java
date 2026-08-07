package com.masterworks.masterworks.client.submitter;

import com.masterworks.masterworks.client.mesh.MeshPoser;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;

public interface Submitter {
    void submit(SubmitNodeCollector collector, MeshPoser poser, PoseStack stack, int packedLight, int packedOverlay);
}
