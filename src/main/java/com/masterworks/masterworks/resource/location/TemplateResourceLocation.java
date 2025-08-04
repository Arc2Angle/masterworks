package com.masterworks.masterworks.resource.location;

import java.util.List;
import com.masterworks.masterworks.Masterworks;
import com.masterworks.masterworks.data.Maps;
import com.masterworks.masterworks.data.composition.Composition;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public record TemplateResourceLocation(ResourceLocation value)
        implements MappedItemResourceLocation<List<Composition>> {

    public static final Codec<TemplateResourceLocation> CODEC =
            TypedResourceLocation.codec(TemplateResourceLocation::new);

    public static final StreamCodec<ByteBuf, TemplateResourceLocation> STREAM_CODEC =
            TypedResourceLocation.streamCodec(TemplateResourceLocation::new);

    public DataMapType<Item, List<Composition>> getDataMapType() {
        return Maps.COMPOSITIONS;
    }

    public static TemplateResourceLocation fromMasterworksAndPath(String path) {
        return new TemplateResourceLocation(Masterworks.resourceLocation(path));
    }
}
