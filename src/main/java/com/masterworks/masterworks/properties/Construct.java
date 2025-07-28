package com.masterworks.masterworks.properties;

import java.util.HashMap;
import java.util.Map;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

/**
 * Represents a dynamic tool's composition.
 */
public record Construct(ResourceLocation template, Map<String, Construct> parts) {

    public static final Codec<Construct> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(ResourceLocation.CODEC.fieldOf("template").forGetter(Construct::template),
                    Codec.dispatchedMap(Codec.STRING, key -> Construct.CODEC).fieldOf("parts")
                            .forGetter(Construct::parts))
            .apply(instance, Construct::new));

    // might require Codec.recursive if Codec.dispatchedMap doesn't lazy load

    public static final StreamCodec<ByteBuf, Construct> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, Construct::template,
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, Construct.STREAM_CODEC, 256),
            Construct::parts, Construct::new);


    // might require StreamCodec.recursive if ByteBufCodecs.map doesn't lazy load
}
