package com.masterworks.masterworks.data;

import com.masterworks.masterworks.location.CompositionReferenceLocation;
import com.masterworks.masterworks.location.ShapeReferenceLocation;
import com.masterworks.masterworks.location.TierReferenceLocation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record Template(
        TierReferenceLocation tier, ShapeReferenceLocation shape, List<CompositionReferenceLocation> compositions) {
    public static final Codec<Template> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    TierReferenceLocation.CODEC.fieldOf("tier").forGetter(Template::tier),
                    ShapeReferenceLocation.CODEC.fieldOf("shape").forGetter(Template::shape),
                    Codec.list(CompositionReferenceLocation.CODEC)
                            .fieldOf("compositions")
                            .forGetter(Template::compositions))
            .apply(instance, Template::new));

    public static final StreamCodec<ByteBuf, Template> STREAM_CODEC = StreamCodec.composite(
            TierReferenceLocation.STREAM_CODEC,
            Template::tier,
            ShapeReferenceLocation.STREAM_CODEC,
            Template::shape,
            CompositionReferenceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()),
            Template::compositions,
            Template::new);
}
