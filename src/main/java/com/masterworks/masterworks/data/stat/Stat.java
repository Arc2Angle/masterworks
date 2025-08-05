package com.masterworks.masterworks.data.stat;

import com.mojang.serialization.Codec;

public enum Stat {
    DURABILITY("durability"), DAMAGE("damage"), ACTION_SPEED("action_speed"), ARMOR(
            "armor"), TOUGHNESS("toughness"), ENCHANTABILITY("enchantability");

    public final String id;

    private Stat(String id) {
        this.id = id;
    }

    public static final Codec<Stat> CODEC = Codec.STRING.xmap(Stat::valueOfId, stat -> stat.id);

    private static Stat valueOfId(String id) {
        try {
            return Stat.valueOf(id.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown stat: " + id, e);
        }
    }

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
