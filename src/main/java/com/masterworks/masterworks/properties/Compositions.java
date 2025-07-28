package com.masterworks.masterworks.properties;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.masterworks.masterworks.data.Registries;
import com.masterworks.masterworks.util.Expression;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

/**
 * Represents the static collection of construct recipes for a template.
 */
public record Compositions(List<Composition> variants) {

    public record Composition(int tier, List<Part> parts, Map<String, Expression> properties) {

        public record Part(String id, Optional<ResourceLocation> shape,
                Optional<ResourceLocation> requires) {

            public static final Codec<Part> CODEC =
                    RecordCodecBuilder
                            .create(instance -> instance
                                    .group(Codec.STRING.fieldOf("id").forGetter(Part::id),
                                            ResourceLocation.CODEC.optionalFieldOf("shape")
                                                    .forGetter(Part::shape),
                                            ResourceLocation.CODEC.optionalFieldOf("requires")
                                                    .forGetter(Part::requires))
                                    .apply(instance, Part::new));
        }

        public static final Codec<Composition> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(Codec.INT.fieldOf("tier").forGetter(Composition::tier),
                        Codec.list(Part.CODEC).fieldOf("parts").forGetter(Composition::parts),
                        Codec.simpleMap(Codec.STRING, Expression.CODEC, Registries.STAT)
                                .fieldOf("properties").forGetter(Composition::properties))
                        .apply(instance, Composition::new));
    }

    public static final Codec<Compositions> CODEC =
            Codec.list(Composition.CODEC).xmap(Compositions::new, Compositions::variants);
}
