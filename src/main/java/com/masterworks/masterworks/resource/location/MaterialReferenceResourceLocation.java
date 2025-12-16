package com.masterworks.masterworks.resource.location;

import com.masterworks.masterworks.data.Material;
import com.masterworks.masterworks.init.MasterworksDataPackRegistries;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record MaterialReferenceResourceLocation(ResourceLocation value)
        implements DataPackRegisteredReferenceResourceLocation<Material> {

    public static final Codec<MaterialReferenceResourceLocation> CODEC = ResourceLocation.CODEC
            .xmap(MaterialReferenceResourceLocation::new, MaterialReferenceResourceLocation::value);

    public static final StreamCodec<ByteBuf, MaterialReferenceResourceLocation> STREAM_CODEC =
            ResourceLocation.STREAM_CODEC.map(MaterialReferenceResourceLocation::new,
                    MaterialReferenceResourceLocation::value);

    public ResourceKey<? extends Registry<Material>> registryKey() {
        return MasterworksDataPackRegistries.MATERIAL;
    }

    public static MaterialReferenceResourceLocation fromNamespaceAndPath(String namespace,
            String path) {
        return new MaterialReferenceResourceLocation(
                ResourceLocation.fromNamespaceAndPath(namespace, path));
    }
}
