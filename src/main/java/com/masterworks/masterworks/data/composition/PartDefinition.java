package com.masterworks.masterworks.data.composition;

import java.util.Optional;
import com.masterworks.masterworks.resource.location.ShapeResourceLocation;
import com.masterworks.masterworks.resource.location.TemplateResourceLocation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record PartDefinition(Optional<ShapeResourceLocation> shape,
        Optional<TemplateResourceLocation> requires) {

    public static final Codec<PartDefinition> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(ShapeResourceLocation.CODEC.optionalFieldOf("shape")
                    .forGetter(PartDefinition::shape),
                    TemplateResourceLocation.CODEC.optionalFieldOf("requires")
                            .forGetter(PartDefinition::requires))
            .apply(instance, PartDefinition::new));
}
