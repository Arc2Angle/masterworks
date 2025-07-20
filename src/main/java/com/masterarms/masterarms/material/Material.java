package com.masterarms.masterarms.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record Material(
                int durability,
                float miningSpeed,
                float attackDamage,
                float armor,
                float toughness,
                int enchantability) {

        public static final Codec<Material> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        Codec.INT.fieldOf("durability").forGetter(Material::durability),
                        Codec.FLOAT.fieldOf("mining_speed").forGetter(Material::miningSpeed),
                        Codec.FLOAT.fieldOf("attack_damage").forGetter(Material::attackDamage),
                        Codec.FLOAT.fieldOf("armor").forGetter(Material::armor),
                        Codec.FLOAT.fieldOf("toughness").forGetter(Material::toughness),
                        Codec.INT.fieldOf("enchantability").forGetter(Material::enchantability))
                        .apply(instance, Material::new));

        public static final StreamCodec<?, Material> STREAM_CODEC = StreamCodec.composite(
                        ByteBufCodecs.INT,
                        Material::durability,
                        ByteBufCodecs.FLOAT,
                        Material::miningSpeed,
                        ByteBufCodecs.FLOAT,
                        Material::attackDamage,
                        ByteBufCodecs.FLOAT,
                        Material::armor,
                        ByteBufCodecs.FLOAT,
                        Material::toughness,
                        ByteBufCodecs.INT,
                        Material::enchantability,
                        Material::new);
}
