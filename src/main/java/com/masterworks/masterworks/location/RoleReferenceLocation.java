package com.masterworks.masterworks.location;

import com.masterworks.masterworks.MasterworksDataPackRegistries;
import com.masterworks.masterworks.data.role.Role;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record RoleReferenceLocation(ResourceLocation value)
        implements DataPackRegisteredReferenceLocation<Role> {

    public static final Codec<RoleReferenceLocation> CODEC =
            ResourceLocation.CODEC.xmap(RoleReferenceLocation::new, RoleReferenceLocation::value);

    public static final StreamCodec<ByteBuf, RoleReferenceLocation> STREAM_CODEC =
            ResourceLocation.STREAM_CODEC.map(RoleReferenceLocation::new,
                    RoleReferenceLocation::value);

    @Override
    public ResourceKey<? extends Registry<Role>> registryKey() {
        return MasterworksDataPackRegistries.ROLE;
    }

    public static RoleReferenceLocation fromNamespaceAndPath(String namespace, String path) {
        return new RoleReferenceLocation(ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

    public static final RoleReferenceLocation MATERIAL =
            fromNamespaceAndPath("masterworks", "material");

    public static final RoleReferenceLocation ITEM = fromNamespaceAndPath("masterworks", "item");
}
