package com.masterworks.masterworks.client.part.submit;

import com.masterworks.masterworks.client.part.PartSubmitter;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import net.minecraft.client.renderer.SubmitNodeCollector;

public record CompositePartSubmitter(Collection<PartSubmitter> components) implements PartSubmitter {
    @Override
    public void submit(
            SubmitNodeCollector collector, Poser poser, PoseStack stack, int packedLight, int packedOverlay) {
        for (PartSubmitter component : components) {
            component.submit(collector, poser, stack, packedLight, packedOverlay);
        }
    }
}
