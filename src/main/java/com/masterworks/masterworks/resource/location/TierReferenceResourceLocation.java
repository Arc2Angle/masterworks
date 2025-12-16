package com.masterworks.masterworks.resource.location;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record TierReferenceResourceLocation(ResourceLocation unqualified)
        implements TextureReferenceResourceLocation {

    public static final Codec<TierReferenceResourceLocation> CODEC = ResourceLocation.CODEC
            .xmap(TierReferenceResourceLocation::new, TierReferenceResourceLocation::unqualified);

    public static final StreamCodec<ByteBuf, TierReferenceResourceLocation> STREAM_CODEC =
            ResourceLocation.STREAM_CODEC.map(TierReferenceResourceLocation::new,
                    TierReferenceResourceLocation::unqualified);

    @Override
    public ResourceLocation value() {
        return unqualified.withPrefix("textures/tier/").withSuffix(".png");
    }

    public static TierReferenceResourceLocation fromNamespaceAndPath(String namespace,
            String path) {
        return new TierReferenceResourceLocation(
                ResourceLocation.fromNamespaceAndPath(namespace, path));
    }
}
