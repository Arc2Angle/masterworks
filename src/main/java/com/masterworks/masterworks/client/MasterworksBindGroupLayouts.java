package com.masterworks.masterworks.client;

import com.mojang.blaze3d.pipeline.BindGroupLayout;

public class MasterworksBindGroupLayouts {
    public static final BindGroupLayout PALETTE_SAMPLER =
            BindGroupLayout.builder().withSampler("PaletteSampler").build();
}
