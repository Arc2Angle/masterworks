package com.masterworks.masterworks.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record MaterialProperties(String name, int durability, float actionSpeed, float attackDamage,
        float armor, float toughness, int enchantability) {

    public static final Codec<MaterialProperties> CODEC =
            RecordCodecBuilder.create(instance -> instance
                    .group(Codec.STRING.fieldOf("name").forGetter(MaterialProperties::name),
                            Codec.INT
                                    .fieldOf("durability")
                                    .forGetter(MaterialProperties::durability),
                            Codec.FLOAT.fieldOf("action_speed")
                                    .forGetter(MaterialProperties::actionSpeed),
                            Codec.FLOAT
                                    .fieldOf("attack_damage")
                                    .forGetter(MaterialProperties::attackDamage),
                            Codec.FLOAT.fieldOf("armor").forGetter(MaterialProperties::armor),
                            Codec.FLOAT.fieldOf("toughness")
                                    .forGetter(MaterialProperties::toughness),
                            Codec.INT.fieldOf("enchantability")
                                    .forGetter(MaterialProperties::enchantability))
                    .apply(instance, MaterialProperties::new));

    public static final MaterialProperties DEFAULT =
            new MaterialProperties("Unknown", 1, 1.0f, 0.0f, 0.0f, 0.0f, 0);
}
