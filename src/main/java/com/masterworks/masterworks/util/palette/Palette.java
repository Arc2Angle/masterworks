package com.masterworks.masterworks.util.palette;

import com.mojang.blaze3d.platform.NativeImage;
import javax.annotation.Nonnull;

/**
 * Represents a color palette, mapping indices to ARGB colors.
 */
public record Palette(int[] colors) {
    public static Palette copyFromImage(@Nonnull NativeImage image) {
        if (image.getHeight() != 1) {
            throw new IllegalArgumentException("Palette image must be 1 pixel tall");
        }

        return new Palette(image.getPixels());
    }

    public int getColorByIndex(int index) {
        return colors[index];
    }

    public int getColorByVoxIndex(int index) {
        return colors[index - 1];
    }
}
