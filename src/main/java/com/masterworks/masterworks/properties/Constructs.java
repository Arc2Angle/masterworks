package com.masterworks.masterworks.properties;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.masterworks.masterworks.data.Registries;
import com.masterworks.masterworks.util.Expression;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record Constructs(List<Construct> variants) {

    public record Construct(int tier, List<Part> parts, Map<String, Expression> properties) {

        public record Part(String id, Optional<ResourceLocation> shape, Optional<String> template) {

            public static final Codec<Part> CODEC = RecordCodecBuilder.create(instance -> instance
                    .group(Codec.STRING.fieldOf("id").forGetter(Part::id),
                            ResourceLocation.CODEC.optionalFieldOf("shape").forGetter(Part::shape),
                            Codec.STRING.optionalFieldOf("template").forGetter(Part::template))
                    .apply(instance, Part::new));

            public Optional<ResourceLocation> getQualifiedShape() {
                return shape.map(s -> s.withPrefix("textures/construct/").withSuffix(".png"));
            }
        }

        public static final Codec<Construct> CODEC = RecordCodecBuilder.create(instance -> instance
                .group(Codec.INT.fieldOf("tier").forGetter(Construct::tier),
                        Codec.list(Part.CODEC).fieldOf("parts").forGetter(Construct::parts),
                        Codec.simpleMap(Codec.STRING, Expression.CODEC, Registries.STAT)
                                .fieldOf("properties").forGetter(Construct::properties))
                .apply(instance, Construct::new));
    }

    public static final Codec<Constructs> CODEC =
            Codec.list(Construct.CODEC).xmap(Constructs::new, Constructs::variants);
}
