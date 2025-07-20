package com.masterarms.masterarms.part.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record PartType(
                float durabilityMultiplier,
                float damageMultiplier,
                float attackSpeedMultiplier) {

        public static final Codec<PartType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        Codec.FLOAT.fieldOf("durability_multiplier").forGetter(PartType::durabilityMultiplier),
                        Codec.FLOAT.fieldOf("damage_multiplier").forGetter(PartType::damageMultiplier),
                        Codec.FLOAT.fieldOf("attack_speed_multiplier").forGetter(PartType::attackSpeedMultiplier))
                        .apply(instance, PartType::new));

        public static final StreamCodec<?, PartType> STREAM_CODEC = StreamCodec.composite(
                        ByteBufCodecs.FLOAT,
                        PartType::durabilityMultiplier,
                        ByteBufCodecs.FLOAT,
                        PartType::damageMultiplier,
                        ByteBufCodecs.FLOAT,
                        PartType::attackSpeedMultiplier,
                        PartType::new);
}
