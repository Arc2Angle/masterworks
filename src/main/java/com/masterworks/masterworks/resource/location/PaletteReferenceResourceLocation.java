package com.masterworks.masterworks.resource.location;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record PaletteReferenceResourceLocation(ResourceLocation unqualified)
        implements TextureReferenceResourceLocation {

    public static final Codec<PaletteReferenceResourceLocation> CODEC = ResourceLocation.CODEC.xmap(
            PaletteReferenceResourceLocation::new, PaletteReferenceResourceLocation::unqualified);

    public static final StreamCodec<ByteBuf, PaletteReferenceResourceLocation> STREAM_CODEC =
            ResourceLocation.STREAM_CODEC.map(PaletteReferenceResourceLocation::new,
                    PaletteReferenceResourceLocation::unqualified);

    @Override
    public ResourceLocation value() {
        return unqualified.withPrefix("textures/palette/").withSuffix(".png");
    }

    public static PaletteReferenceResourceLocation fromNamespaceAndPath(String namespace,
            String path) {
        return new PaletteReferenceResourceLocation(
                ResourceLocation.fromNamespaceAndPath(namespace, path));
    }
}
