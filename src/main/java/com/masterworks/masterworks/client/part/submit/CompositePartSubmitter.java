package com.masterworks.masterworks.client.part.submit;

import com.masterworks.masterworks.client.part.PartSubmitter;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.SequencedCollection;
import net.minecraft.client.renderer.SubmitNodeCollector;

public record CompositePartSubmitter(Collection<PartSubmitter> components) implements PartSubmitter {
    @Override
    public void submit(
            SubmitNodeCollector collector, Poser poser, PoseStack stack, int packedLight, int packedOverlay) {
        for (PartSubmitter component : components) {
            component.submit(collector, poser, stack, packedLight, packedOverlay);
        }
    }

    public static PartSubmitter of(SequencedCollection<PartSubmitter> components) {
        switch (components.size()) {
            case 0 -> throw new IllegalArgumentException("Cannot create a CompositePartSubmitter with no components");
            case 1 -> {
                return components.getFirst();
            }
            default -> {
                return new CompositePartSubmitter(components);
            }
        }
    }
}
