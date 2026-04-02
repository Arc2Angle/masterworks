package com.masterworks.masterworks.client.etching;

import com.mojang.blaze3d.platform.NativeImage;

public record Etching(int x, int y, boolean[] values) {
    public static Etching copyFromImage(NativeImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        boolean[] values = new boolean[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                values[(height - y - 1) * width + x] = image.getLuminanceOrAlpha(x, y) != 0;
            }
        }
        return new Etching(width, height, values);
    }
}
