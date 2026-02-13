package com.masterworks.masterworks.client.resource.reference;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

public record VoxFileResourceReference(ResourceLocation id) implements ResourceReference {
    public static final Codec<VoxFileResourceReference> CODEC =
            ResourceLocation.CODEC.xmap(VoxFileResourceReference::new, VoxFileResourceReference::id);
}
