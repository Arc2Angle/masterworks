package com.masterworks.masterworks.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Material(String name, int durability, float miningSpeed, float attackDamage,
                float armor, float toughness, int enchantability) {

        public static final Codec<Material> CODEC = RecordCodecBuilder.create(instance -> instance
                        .group(Codec.STRING.fieldOf("name").forGetter(Material::name),
                                        Codec.INT.fieldOf("durability")
                                                        .forGetter(Material::durability),
                                        Codec.FLOAT.fieldOf("mining_speed")
                                                        .forGetter(Material::miningSpeed),
                                        Codec.FLOAT.fieldOf("attack_damage")
                                                        .forGetter(Material::attackDamage),
                                        Codec.FLOAT.fieldOf("armor").forGetter(Material::armor),
                                        Codec.FLOAT.fieldOf("toughness")
                                                        .forGetter(Material::toughness),
                                        Codec.INT.fieldOf("enchantability")
                                                        .forGetter(Material::enchantability))
                        .apply(instance, Material::new));
}
