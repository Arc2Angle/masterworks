package com.masterworks.masterworks.data.composition;

import java.util.List;
import java.util.Map;
import com.masterworks.masterworks.data.Registries;
import com.masterworks.masterworks.util.Expression;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Composition(int tier, List<PartDefinition> parts, Map<Stat, Expression> properties) {

    public static final Codec<Composition> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.INT.fieldOf("tier").forGetter(Composition::tier),
                    Codec.list(PartDefinition.CODEC).fieldOf("parts").forGetter(Composition::parts),
                    Codec.simpleMap(Stat.CODEC, Expression.CODEC, Registries.STAT)
                            .fieldOf("properties").forGetter(Composition::properties))
            .apply(instance, Composition::new));

}
