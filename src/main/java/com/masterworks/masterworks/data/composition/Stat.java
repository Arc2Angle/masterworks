package com.masterworks.masterworks.data.composition;

import com.mojang.serialization.Codec;

public enum Stat {
    DURABILITY("durability"), DAMAGE("damage"), ACTION_SPEED("action_speed"), ARMOR(
            "armor"), TOUGHNESS("toughness"), ENCHANTABILITY("enchantability");

    public final String id;

    private Stat(String id) {
        this.id = id;
    }

    public static final Codec<Stat> CODEC = Codec.STRING.xmap(Stat::valueOf, stat -> stat.id);

    /**
     * Thrown when a stat is not relevant to a specific carrier instance which can generally have
     * any set of stats available.
     */
    public class IrrelevantException extends RuntimeException {
        public IrrelevantException(String carrier) {
            super("Stat " + id + " is not relevant for: " + carrier);
        }
    }

}
