package com.masterworks.masterworks.data.composition;

import java.util.Optional;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record Part(String name, Optional<ResourceLocation> shape,
        Optional<ResourceLocation> requires) {

    public static final Codec<Part> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.STRING.fieldOf("name").forGetter(Part::name),
                    ResourceLocation.CODEC.optionalFieldOf("shape").forGetter(Part::shape),
                    ResourceLocation.CODEC.optionalFieldOf("requires").forGetter(Part::requires))
            .apply(instance, Part::new));
}
