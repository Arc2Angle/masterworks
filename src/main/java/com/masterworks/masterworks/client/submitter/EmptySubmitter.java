package com.masterworks.masterworks.client.submitter;

import com.masterworks.masterworks.client.mesh.MeshPoser;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;

public record EmptySubmitter() implements Submitter {
    @Override
    public void submit(
            SubmitNodeCollector collector, MeshPoser poser, PoseStack stack, int packedLight, int packedOverlay) {}
}
