package com.masterworks.masterworks.location;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record TierReferenceLocation(ResourceLocation unqualified)
        implements TextureReferenceLocation {

    public static final Codec<TierReferenceLocation> CODEC = ResourceLocation.CODEC
            .xmap(TierReferenceLocation::new, TierReferenceLocation::unqualified);

    public static final StreamCodec<ByteBuf, TierReferenceLocation> STREAM_CODEC =
            ResourceLocation.STREAM_CODEC.map(TierReferenceLocation::new,
                    TierReferenceLocation::unqualified);

    @Override
    public ResourceLocation value() {
        return unqualified.withPrefix("textures/tier/").withSuffix(".png");
    }

    public static TierReferenceLocation fromNamespaceAndPath(String namespace, String path) {
        return new TierReferenceLocation(ResourceLocation.fromNamespaceAndPath(namespace, path));
    }
}
