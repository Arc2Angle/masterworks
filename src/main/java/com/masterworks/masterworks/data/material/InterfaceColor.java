package com.masterworks.masterworks.data.material;

import com.mojang.serialization.Codec;

public record InterfaceColor(int argb) {

    public static final Codec<InterfaceColor> CODEC =
            Codec.STRING.xmap(InterfaceColor::parse, InterfaceColor::format);

    public static InterfaceColor parse(String value) {
        return new InterfaceColor((int) Long.parseLong(value, 16));
    }

    public String format() {
        return String.format("%08X", argb);
    }
}
