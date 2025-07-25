package com.masterworks.masterworks.properties.tool.part.type;

import com.masterworks.masterworks.Masterworks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record ToolPartTypeProperties(String name, ResourceLocation shape,
        float durabilityMultiplier, float damageMultiplier, float actionSpeedMultiplier) {

    public static final Codec<ToolPartTypeProperties> CODEC =
            RecordCodecBuilder.create(instance -> instance
                    .group(Codec.STRING.fieldOf("name").forGetter(ToolPartTypeProperties::name),
                            ResourceLocation.CODEC.fieldOf("shape")
                                    .forGetter(ToolPartTypeProperties::shape),
                            Codec.FLOAT.fieldOf("durability_multiplier")
                                    .forGetter(ToolPartTypeProperties::durabilityMultiplier),
                            Codec.FLOAT.fieldOf("damage_multiplier")
                                    .forGetter(ToolPartTypeProperties::damageMultiplier),
                            Codec.FLOAT.fieldOf("action_speed_multiplier")
                                    .forGetter(ToolPartTypeProperties::actionSpeedMultiplier))
                    .apply(instance, ToolPartTypeProperties::new));

    public static final ToolPartTypeProperties DEFAULT = new ToolPartTypeProperties("Unknown",
            ResourceLocation.fromNamespaceAndPath(Masterworks.MOD_ID, "default"), 0.0f, 0.0f, 0.0f);
}
