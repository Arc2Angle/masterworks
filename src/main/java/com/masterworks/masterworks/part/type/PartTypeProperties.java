package com.masterworks.masterworks.part.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record PartTypeProperties(String name, float durabilityMultiplier, float damageMultiplier,
        float actionSpeedMultiplier) {

    public static final Codec<PartTypeProperties> CODEC =
            RecordCodecBuilder.create(instance -> instance
                    .group(Codec.STRING.fieldOf("name").forGetter(PartTypeProperties::name),
                            Codec.FLOAT.fieldOf("durability_multiplier")
                                    .forGetter(PartTypeProperties::durabilityMultiplier),
                            Codec.FLOAT.fieldOf("damage_multiplier")
                                    .forGetter(PartTypeProperties::damageMultiplier),
                            Codec.FLOAT.fieldOf("action_speed_multiplier")
                                    .forGetter(PartTypeProperties::actionSpeedMultiplier))
                    .apply(instance, PartTypeProperties::new));
}
