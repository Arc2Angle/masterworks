package com.masterworks.masterworks.resource.location;

import com.masterworks.masterworks.data.Composition;
import com.masterworks.masterworks.init.MasterworksDataPackRegistries;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record CompositionReferenceResourceLocation(ResourceLocation value)
        implements DataPackRegisteredReferenceResourceLocation<Composition> {

    public static final Codec<CompositionReferenceResourceLocation> CODEC =
            ResourceLocation.CODEC.xmap(CompositionReferenceResourceLocation::new,
                    CompositionReferenceResourceLocation::value);

    public static final StreamCodec<ByteBuf, CompositionReferenceResourceLocation> STREAM_CODEC =
            ResourceLocation.STREAM_CODEC.map(CompositionReferenceResourceLocation::new,
                    CompositionReferenceResourceLocation::value);

    public ResourceKey<? extends Registry<Composition>> registryKey() {
        return MasterworksDataPackRegistries.COMPOSITION;
    }

    public static CompositionReferenceResourceLocation fromNamespaceAndPath(String namespace,
            String path) {
        return new CompositionReferenceResourceLocation(
                ResourceLocation.fromNamespaceAndPath(namespace, path));
    }
}
