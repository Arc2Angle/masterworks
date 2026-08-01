package com.masterworks.masterworks.client.part.submit;

import com.masterworks.masterworks.client.part.PartSubmitter;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import net.minecraft.client.renderer.SubmitNodeCollector;

public record NamedPosePartSubmitter(Map<String, PartSubmitter> children) implements PartSubmitter {
    @Override
    public void submit(
            SubmitNodeCollector collector, Poser poser, PoseStack stack, int packedLight, int packedOverlay) {
        for (Map.Entry<String, PartSubmitter> entry : children.entrySet()) {
            String name = entry.getKey();
            PartSubmitter child = entry.getValue();

            stack.pushPose();
            Poser subposer = poser.enter(name, stack);
            child.submit(collector, subposer, stack, packedLight, packedOverlay);
            stack.popPose();
        }
    }
}
