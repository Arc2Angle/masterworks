package com.masterworks.masterworks.properties.tool.part.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ToolPartTypeProperties(String name, float durabilityMultiplier,
        float damageMultiplier, float actionSpeedMultiplier) {

    public static final Codec<ToolPartTypeProperties> CODEC =
            RecordCodecBuilder.create(instance -> instance
                    .group(Codec.STRING.fieldOf("name").forGetter(ToolPartTypeProperties::name),
                            Codec.FLOAT.fieldOf("durability_multiplier")
                                    .forGetter(ToolPartTypeProperties::durabilityMultiplier),
                            Codec.FLOAT.fieldOf("damage_multiplier")
                                    .forGetter(ToolPartTypeProperties::damageMultiplier),
                            Codec.FLOAT.fieldOf("action_speed_multiplier")
                                    .forGetter(ToolPartTypeProperties::actionSpeedMultiplier))
                    .apply(instance, ToolPartTypeProperties::new));
}
