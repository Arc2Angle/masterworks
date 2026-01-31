package com.masterworks.masterworks.location;

import com.masterworks.masterworks.MasterworksDataPackRegistries;
import com.masterworks.masterworks.data.Composition;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record CompositionReferenceLocation(ResourceLocation value)
        implements DataPackRegisteredReferenceLocation<Composition> {

    public static final Codec<CompositionReferenceLocation> CODEC =
            ResourceLocation.CODEC.xmap(CompositionReferenceLocation::new, CompositionReferenceLocation::value);

    public static final StreamCodec<ByteBuf, CompositionReferenceLocation> STREAM_CODEC =
            ResourceLocation.STREAM_CODEC.map(CompositionReferenceLocation::new, CompositionReferenceLocation::value);

    public ResourceKey<? extends Registry<Composition>> registryKey() {
        return MasterworksDataPackRegistries.COMPOSITION;
    }

    public static CompositionReferenceLocation fromNamespaceAndPath(String namespace, String path) {
        return new CompositionReferenceLocation(ResourceLocation.fromNamespaceAndPath(namespace, path));
    }
}
