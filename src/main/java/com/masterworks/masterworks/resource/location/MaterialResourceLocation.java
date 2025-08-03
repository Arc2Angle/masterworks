package com.masterworks.masterworks.resource.location;

import com.masterworks.masterworks.data.Maps;
import com.masterworks.masterworks.data.material.Material;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public record MaterialResourceLocation(ResourceLocation value)
        implements MappedItemResourceLocation<Material> {

    public static final Codec<MaterialResourceLocation> CODEC =
            TypedResourceLocation.codec(MaterialResourceLocation::new);

    public static final StreamCodec<ByteBuf, MaterialResourceLocation> STREAM_CODEC =
            TypedResourceLocation.streamCodec(MaterialResourceLocation::new);

    public DataMapType<Item, Material> getDataMapType() {
        return Maps.MATERIALS;
    }
}
