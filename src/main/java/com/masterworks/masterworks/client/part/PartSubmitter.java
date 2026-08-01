package com.masterworks.masterworks.client.part;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;

public interface PartSubmitter {
    void submit(SubmitNodeCollector collector, Poser poser, PoseStack stack, int packedLight, int packedOverlay);

    interface Poser {
        Poser enter(String name, PoseStack stack);
    }
}
