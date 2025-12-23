package com.masterworks.masterworks.location;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ShapeReferenceLocation(ResourceLocation unqualified)
        implements TextureReferenceLocation {

    public static final Codec<ShapeReferenceLocation> CODEC = ResourceLocation.CODEC
            .xmap(ShapeReferenceLocation::new, ShapeReferenceLocation::unqualified);

    public static final StreamCodec<ByteBuf, ShapeReferenceLocation> STREAM_CODEC =
            ResourceLocation.STREAM_CODEC.map(ShapeReferenceLocation::new,
                    ShapeReferenceLocation::unqualified);

    @Override
    public ResourceLocation value() {
        return unqualified.withPrefix("textures/shape/").withSuffix(".png");
    }

    public static ShapeReferenceLocation fromNamespaceAndPath(String namespace, String path) {
        return new ShapeReferenceLocation(ResourceLocation.fromNamespaceAndPath(namespace, path));
    }
}
