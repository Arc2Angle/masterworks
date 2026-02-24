package com.masterworks.masterworks.util.palette;

import com.mojang.blaze3d.platform.NativeImage;
import javax.annotation.Nonnull;

/**
 * Represents a color palette, mapping indices to ARGB colors.
 */
public record Palette(int[] colors) {
    public static final Palette NO_COLOR8 = new Palette(
            new int[] {0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF});

    public static Palette copyFromImage(@Nonnull NativeImage image) {
        if (image.getHeight() != 1) {
            throw new IllegalArgumentException("Palette image must be 1 pixel tall");
        }

        return new Palette(image.getPixels());
    }

    public static Palette fromRGBA(int[] rgba) {
        int[] argb = new int[rgba.length];

        for (int i = 0; i < rgba.length; i++) {
            argb[i] = Integer.rotateRight(rgba[i], 8);
        }

        return new Palette(argb);
    }

    public int getColorByIndex(int index) {
        return colors[index];
    }

    public int getColorByVoxIndex(int index) {
        return colors[index - 1];
    }
}
