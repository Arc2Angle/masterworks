package com.masterworks.masterworks.resource.location;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ShapeReferenceResourceLocation(ResourceLocation unqualified)
        implements TextureReferenceResourceLocation {

    public static final Codec<ShapeReferenceResourceLocation> CODEC = ResourceLocation.CODEC
            .xmap(ShapeReferenceResourceLocation::new, ShapeReferenceResourceLocation::unqualified);

    public static final StreamCodec<ByteBuf, ShapeReferenceResourceLocation> STREAM_CODEC =
            ResourceLocation.STREAM_CODEC.map(ShapeReferenceResourceLocation::new,
                    ShapeReferenceResourceLocation::unqualified);

    @Override
    public ResourceLocation value() {
        return unqualified.withPrefix("textures/shape/").withSuffix(".png");
    }

    public static ShapeReferenceResourceLocation fromNamespaceAndPath(String namespace,
            String path) {
        return new ShapeReferenceResourceLocation(
                ResourceLocation.fromNamespaceAndPath(namespace, path));
    }
}
