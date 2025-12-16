package com.masterworks.masterworks.resource.location;

import com.masterworks.masterworks.data.role.Role;
import com.masterworks.masterworks.init.MasterworksDataPackRegistries;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record RoleReferenceResourceLocation(ResourceLocation value)
        implements DataPackRegisteredReferenceResourceLocation<Role> {

    public static final Codec<RoleReferenceResourceLocation> CODEC = ResourceLocation.CODEC
            .xmap(RoleReferenceResourceLocation::new, RoleReferenceResourceLocation::value);

    public static final StreamCodec<ByteBuf, RoleReferenceResourceLocation> STREAM_CODEC =
            ResourceLocation.STREAM_CODEC.map(RoleReferenceResourceLocation::new,
                    RoleReferenceResourceLocation::value);

    @Override
    public ResourceKey<? extends Registry<Role>> registryKey() {
        return MasterworksDataPackRegistries.ROLE;
    }

    public static RoleReferenceResourceLocation fromNamespaceAndPath(String namespace,
            String path) {
        return new RoleReferenceResourceLocation(
                ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

    public static final RoleReferenceResourceLocation MATERIAL =
            fromNamespaceAndPath("masterworks", "material");

    public static final RoleReferenceResourceLocation ITEM =
            fromNamespaceAndPath("masterworks", "item");
}
