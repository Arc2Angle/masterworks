package com.masterworks.masterworks.resource.location;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record TierResourceLocation(ResourceLocation unqualified)
        implements TextureResourceLocation {

    public static final Codec<TierResourceLocation> CODEC =
            TypedResourceLocation.codec(TierResourceLocation::new);

    public static final StreamCodec<ByteBuf, TierResourceLocation> STREAM_CODEC =
            TypedResourceLocation.streamCodec(TierResourceLocation::new);

    @Override
    public ResourceLocation value() {
        return unqualified.withPrefix("textures/template/").withSuffix(".png");
    }

}
