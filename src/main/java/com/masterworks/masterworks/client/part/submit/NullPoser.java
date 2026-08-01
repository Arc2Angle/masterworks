package com.masterworks.masterworks.client.part.submit;

import com.masterworks.masterworks.client.part.PartSubmitter;
import com.masterworks.masterworks.client.part.PartSubmitter.Poser;
import com.mojang.blaze3d.vertex.PoseStack;

public final class NullPoser implements PartSubmitter.Poser {
    @Override
    public Poser enter(String name, PoseStack stack) {
        throw new UnsupportedOperationException("NullPoser does not have children");
    }
}
