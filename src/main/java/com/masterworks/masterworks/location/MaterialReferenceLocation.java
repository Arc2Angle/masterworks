package com.masterworks.masterworks.location;

import com.masterworks.masterworks.MasterworksDataPackRegistries;
import com.masterworks.masterworks.data.Material;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record MaterialReferenceLocation(ResourceLocation value)
        implements DataPackRegisteredReferenceLocation<Material> {

    public static final Codec<MaterialReferenceLocation> CODEC =
            ResourceLocation.CODEC.xmap(MaterialReferenceLocation::new, MaterialReferenceLocation::value);

    public static final StreamCodec<ByteBuf, MaterialReferenceLocation> STREAM_CODEC =
            ResourceLocation.STREAM_CODEC.map(MaterialReferenceLocation::new, MaterialReferenceLocation::value);

    public ResourceKey<? extends Registry<Material>> registryKey() {
        return MasterworksDataPackRegistries.MATERIAL;
    }

    public static MaterialReferenceLocation fromNamespaceAndPath(String namespace, String path) {
        return new MaterialReferenceLocation(ResourceLocation.fromNamespaceAndPath(namespace, path));
    }
}
