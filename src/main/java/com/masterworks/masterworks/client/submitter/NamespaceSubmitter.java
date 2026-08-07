package com.masterworks.masterworks.client.submitter;

import com.masterworks.masterworks.client.mesh.MeshPoser;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.client.renderer.SubmitNodeCollector;

public record NamespaceSubmitter(Map<String, Submitter> map) implements Submitter {
    @Override
    public void submit(
            SubmitNodeCollector collector, MeshPoser poser, PoseStack stack, int packedLight, int packedOverlay) {
        for (Map.Entry<String, Submitter> entry : map.entrySet()) {
            String name = entry.getKey();
            Submitter child = entry.getValue();

            stack.pushPose();
            MeshPoser subposer = poser.enter(name, stack);
            child.submit(collector, subposer, stack, packedLight, packedOverlay);
            stack.popPose();
        }
    }
}
