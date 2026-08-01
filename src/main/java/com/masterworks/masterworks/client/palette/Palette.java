package com.masterworks.masterworks.client.palette;

/**
 * Represents a color palette, mapping indices to ARGB colors.
 */
public record Palette(int[] colors) {
    public static final Palette NO_COLOR8 = new Palette(
            new int[] {0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF});

    public static Palette fromRGBA(int[] rgba) {
        int[] argb = new int[rgba.length];

        for (int i = 0; i < rgba.length; i++) {
            argb[i] = Integer.rotateRight(rgba[i], 8);
        }

        return new Palette(argb);
    }
}
