package com.masterworks.masterworks.client.resource.reference;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

public record PaletteResourceReference(ResourceLocation id) implements ResourceReference {
    public static final Codec<PaletteResourceReference> CODEC =
            ResourceLocation.CODEC.xmap(PaletteResourceReference::new, PaletteResourceReference::id);
}
