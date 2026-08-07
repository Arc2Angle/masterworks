package com.masterworks.masterworks.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.ARGB;

public final class Color {
    private final int argb;

    private Color(int argb) {
        this.argb = argb;
    }

    @Override
    public String toString() {
        return formatARGB();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Color color) {
            return this.argb == color.argb;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return argb;
    }

    public static final Codec<Color> ARGB_CODEC = Codec.STRING.xmap(Color::parseARGB, Color::formatARGB);

    public int argb() {
        return argb;
    }

    public static Color parseARGB(String argb) {
        // necessary because technically opaque colors are 0xFFxxxxxx, which does not fit in a signed int
        return fromARGB((int) Long.parseLong(argb, 16));
    }

    public String formatARGB() {
        return String.format("%08X", argb());
    }

    public static Color fromARGB(int argb) {
        return new Color(argb);
    }

    public static final Codec<Color> ABGR_CODEC = Codec.STRING.xmap(Color::parseABGR, Color::formatABGR);

    public int abgr() {
        return ARGB.toABGR(argb);
    }

    public static Color parseABGR(String abgr) {
        // necessary because technically opaque colors are 0xFFxxxxxx, which does not fit in a signed int
        return fromABGR((int) Long.parseLong(abgr, 16));
    }

    public String formatABGR() {
        return String.format("%08X", abgr());
    }

    public static Color fromABGR(int abgr) {
        return new Color(ARGB.fromABGR(abgr));
    }
}
