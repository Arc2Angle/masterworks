package com.masterworks.masterworks.resource.location;

import java.util.function.Function;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public interface TypedResourceLocation {
    public ResourceLocation value();

    public static <T extends TypedResourceLocation> Codec<T> codec(
            Function<ResourceLocation, ? extends T> factory) {
        return ResourceLocation.CODEC.xmap(factory, TypedResourceLocation::value);
    }

    public static <T extends TypedResourceLocation> StreamCodec<ByteBuf, T> streamCodec(
            Function<ResourceLocation, ? extends T> factory) {
        return ResourceLocation.STREAM_CODEC.map(factory, TypedResourceLocation::value);
    }
}
