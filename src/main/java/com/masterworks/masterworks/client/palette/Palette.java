package com.masterworks.masterworks.client.palette;

import com.mojang.serialization.Codec;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;

/**
 * Represents a color palette, mapping indices to ARGB colors.
 */
public record Palette(Identifier id) {
    public static final Codec<Palette> CODEC = Identifier.CODEC.xmap(Palette::new, Palette::id);

    private static final FileToIdConverter CONVERTER = new FileToIdConverter("textures/palette", ".png");

    public Identifier location() {
        return CONVERTER.idToFile(id);
    }
}
