package com.masterworks.masterworks.resource.location;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record PaletteResourceLocation(ResourceLocation unqualified)
        implements TextureResourceLocation {

    public static final Codec<PaletteResourceLocation> CODEC =
            TypedResourceLocation.codec(PaletteResourceLocation::new);

    public static final StreamCodec<ByteBuf, PaletteResourceLocation> STREAM_CODEC =
            TypedResourceLocation.streamCodec(PaletteResourceLocation::new);

    @Override
    public ResourceLocation value() {
        return unqualified.withPrefix("textures/material/").withSuffix(".png");
    }

    public static final PaletteResourceLocation DEFAULT = new PaletteResourceLocation(
            ResourceLocation.fromNamespaceAndPath("masterworks", "none"));
}
