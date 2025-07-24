package com.masterworks.masterworks.properties.tool.part.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ToolPartMaterialProperties(String name, int durability, float actionSpeed,
        float attackDamage, float armor, float toughness, int enchantability) {

    public static final Codec<ToolPartMaterialProperties> CODEC =
            RecordCodecBuilder.create(instance -> instance
                    .group(Codec.STRING.fieldOf("name").forGetter(ToolPartMaterialProperties::name),
                            Codec.INT
                                    .fieldOf("durability")
                                    .forGetter(ToolPartMaterialProperties::durability),
                            Codec.FLOAT.fieldOf("action_speed")
                                    .forGetter(ToolPartMaterialProperties::actionSpeed),
                            Codec.FLOAT.fieldOf("attack_damage")
                                    .forGetter(ToolPartMaterialProperties::attackDamage),
                            Codec.FLOAT.fieldOf("armor")
                                    .forGetter(ToolPartMaterialProperties::armor),
                            Codec.FLOAT.fieldOf("toughness")
                                    .forGetter(ToolPartMaterialProperties::toughness),
                            Codec.INT.fieldOf("enchantability")
                                    .forGetter(ToolPartMaterialProperties::enchantability))
                    .apply(instance, ToolPartMaterialProperties::new));

    public static final ToolPartMaterialProperties DEFAULT =
            new ToolPartMaterialProperties("Unknown", 1, 1.0f, 0.0f, 0.0f, 0.0f, 0);
}
