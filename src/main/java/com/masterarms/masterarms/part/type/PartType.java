package com.masterarms.masterarms.part.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record PartType(String name, float durabilityMultiplier, float damageMultiplier,
                float attackSpeedMultiplier) {

        public static final Codec<PartType> CODEC = RecordCodecBuilder.create(instance -> instance
                        .group(Codec.STRING.fieldOf("name").forGetter(PartType::name),
                                        Codec.FLOAT.fieldOf("durability_multiplier")
                                                        .forGetter(PartType::durabilityMultiplier),
                                        Codec.FLOAT.fieldOf("damage_multiplier")
                                                        .forGetter(PartType::damageMultiplier),
                                        Codec.FLOAT.fieldOf("attack_speed_multiplier")
                                                        .forGetter(PartType::attackSpeedMultiplier))
                        .apply(instance, PartType::new));
}
