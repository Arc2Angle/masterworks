package com.masterworks.masterworks.location;

import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.MasterworksRegistries;
import com.masterworks.masterworks.data.property.Property;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record PropertyTypeReferenceLocation(String unvalidated)
        implements RegisteredReferenceLocation<Property.Type<?>> {

    public static final Codec<PropertyTypeReferenceLocation> CODEC = Codec.STRING
            .xmap(PropertyTypeReferenceLocation::new, PropertyTypeReferenceLocation::unvalidated);

    public static final StreamCodec<ByteBuf, PropertyTypeReferenceLocation> STREAM_CODEC =
            ByteBufCodecs.STRING_UTF8.map(PropertyTypeReferenceLocation::new,
                    PropertyTypeReferenceLocation::unvalidated);

    static final String EXTENDS = "masterworks:*";

    @Override
    public ResourceLocation value() {
        if (unvalidated.equals(EXTENDS)) {
            return ResourceLocation.fromNamespaceAndPath(MasterworksMod.ID, "extends");
        }

        return ResourceLocation.parse(unvalidated);
    }

    public Registry<Property.Type<?>> registry() {
        return MasterworksRegistries.PROPERTY_TYPE;
    }
}
