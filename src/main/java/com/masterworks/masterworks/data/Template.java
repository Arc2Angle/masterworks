package com.masterworks.masterworks.data;

import java.util.List;
import com.masterworks.masterworks.resource.location.CompositionReferenceResourceLocation;
import com.masterworks.masterworks.resource.location.ShapeReferenceResourceLocation;
import com.masterworks.masterworks.resource.location.TierReferenceResourceLocation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record Template(TierReferenceResourceLocation tier, ShapeReferenceResourceLocation shape,
        List<CompositionReferenceResourceLocation> compositions) {
    public static final Codec<Template> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(TierReferenceResourceLocation.CODEC.fieldOf("tier").forGetter(Template::tier),
                    ShapeReferenceResourceLocation.CODEC.fieldOf("shape")
                            .forGetter(Template::shape),
                    Codec.list(CompositionReferenceResourceLocation.CODEC).fieldOf("compositions")
                            .forGetter(Template::compositions))
            .apply(instance, Template::new));

    public static final StreamCodec<ByteBuf, Template> STREAM_CODEC =
            StreamCodec.composite(TierReferenceResourceLocation.STREAM_CODEC, Template::tier,
                    ShapeReferenceResourceLocation.STREAM_CODEC, Template::shape,
                    CompositionReferenceResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    Template::compositions, Template::new);
}
