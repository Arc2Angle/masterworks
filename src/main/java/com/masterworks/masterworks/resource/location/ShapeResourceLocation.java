package com.masterworks.masterworks.resource.location;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ShapeResourceLocation(ResourceLocation unqualified)
        implements TextureResourceLocation {

    public static final Codec<ShapeResourceLocation> CODEC =
            TypedResourceLocation.codec(ShapeResourceLocation::new);

    public static final StreamCodec<ByteBuf, ShapeResourceLocation> STREAM_CODEC =
            TypedResourceLocation.streamCodec(ShapeResourceLocation::new);

    @Override
    public ResourceLocation value() {
        return unqualified.withPrefix("textures/construct/").withSuffix(".png");
    }

    public static final ShapeResourceLocation DEFAULT =
            new ShapeResourceLocation(ResourceLocation.fromNamespaceAndPath("masterworks", "orb"));
}
