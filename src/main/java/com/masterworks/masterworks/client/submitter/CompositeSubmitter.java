package com.masterworks.masterworks.client.submitter;

import com.masterworks.masterworks.client.mesh.MeshPoser;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Collection;
import java.util.SequencedCollection;
import net.minecraft.client.renderer.SubmitNodeCollector;

public record CompositeSubmitter(Collection<? extends Submitter> components) implements Submitter {
    @Override
    public void submit(
            SubmitNodeCollector collector, MeshPoser poser, PoseStack stack, int packedLight, int packedOverlay) {
        for (Submitter component : components) {
            component.submit(collector, poser, stack, packedLight, packedOverlay);
        }
    }

    public static Submitter of(SequencedCollection<? extends Submitter> components) {
        switch (components.size()) {
            case 0 -> throw new IllegalArgumentException("Cannot create a CompositePartSubmitter with no components");
            case 1 -> {
                return components.getFirst();
            }
            default -> {
                return new CompositeSubmitter(components);
            }
        }
    }
}
