package com.masterworks.masterworks.location;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record PaletteReferenceLocation(ResourceLocation unqualified)
        implements TextureReferenceLocation {

    public static final Codec<PaletteReferenceLocation> CODEC = ResourceLocation.CODEC
            .xmap(PaletteReferenceLocation::new, PaletteReferenceLocation::unqualified);

    public static final StreamCodec<ByteBuf, PaletteReferenceLocation> STREAM_CODEC =
            ResourceLocation.STREAM_CODEC.map(PaletteReferenceLocation::new,
                    PaletteReferenceLocation::unqualified);

    @Override
    public ResourceLocation value() {
        return unqualified.withPrefix("textures/palette/").withSuffix(".png");
    }

    public static PaletteReferenceLocation fromNamespaceAndPath(String namespace, String path) {
        return new PaletteReferenceLocation(ResourceLocation.fromNamespaceAndPath(namespace, path));
    }
}
