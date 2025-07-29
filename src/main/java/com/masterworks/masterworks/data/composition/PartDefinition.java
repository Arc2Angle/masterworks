package com.masterworks.masterworks.data.composition;

import java.util.Optional;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record PartDefinition(Optional<ResourceLocation> shape,
        Optional<ResourceLocation> requires) {

    public static final Codec<PartDefinition> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(ResourceLocation.CODEC.optionalFieldOf("shape").forGetter(PartDefinition::shape),
                    ResourceLocation.CODEC.optionalFieldOf("requires")
                            .forGetter(PartDefinition::requires))
            .apply(instance, PartDefinition::new));

    public ResourceLocation getQualifiedShapeOrThrow() {
        return shape.orElseThrow(() -> new IllegalStateException("Shapeless part definition."))
                .withPrefix("textures/part/material/").withSuffix(".png");
    }
}
