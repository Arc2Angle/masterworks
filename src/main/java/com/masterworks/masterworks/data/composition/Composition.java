package com.masterworks.masterworks.data.composition;

import com.masterworks.masterworks.util.Expression;
import java.util.List;
import java.util.Map;
import com.masterworks.masterworks.data.Registries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Composition(int tier, List<Part> parts, Map<String, Expression> properties) {

    public static final Codec<Composition> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.INT.fieldOf("tier").forGetter(Composition::tier),
                    Codec.list(Part.CODEC).fieldOf("parts").forGetter(Composition::parts),
                    Codec.simpleMap(Codec.STRING, Expression.CODEC, Registries.STAT)
                            .fieldOf("properties").forGetter(Composition::properties))
            .apply(instance, Composition::new));

}
