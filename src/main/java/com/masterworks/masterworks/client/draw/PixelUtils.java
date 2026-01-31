package com.masterworks.masterworks.client.draw;

import com.mojang.blaze3d.platform.NativeImage;
import java.util.function.IntUnaryOperator;
import net.minecraft.util.ARGB;

public class PixelUtils {
    private PixelUtils() {}

    // Overlays 'top' image over 'bottom' image such that fully transparent pixels
    // in 'top' reveal the corresponding pixel from 'bottom'.
    public static NativeImage Overlay(NativeImage bottom, NativeImage top) {
        int width = bottom.getWidth();
        int height = bottom.getHeight();

        NativeImage result = new NativeImage(width, height, false);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int topPixel = top.getPixel(x, y);
                int bottomPixel = bottom.getPixel(x, y);

                result.setPixel(x, y, ARGB.alpha(topPixel) != 0 ? topPixel : bottomPixel);
            }
        }

        return result;
    }

    // Shadows 'image' using 'silhouette' with the given normalized shadow scale
    // Each pixel in 'image' is scaled by either 100% (if the corresponding pixel
    // in 'silhouette' is fully transparent) or 'normalizedShadowScale' (if the
    // corresponding pixel in 'silhouette' is opaque)
    public static NativeImage Shadow(NativeImage image, NativeImage silhouette, float normalizedShadowScale) {
        int width = image.getWidth();
        int height = image.getHeight();
        int shadowScale = (int) (normalizedShadowScale * 0xFF);

        NativeImage result = new NativeImage(width, height, true);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int imageColor = image.getPixel(x, y);

                if (ARGB.alpha(imageColor) == 0) {
                    continue;
                }

                int silhouetteColor = silhouette.getPixel(x, y);
                int scale = ARGB.alpha(silhouetteColor) == 0 ? 0xFF : shadowScale;
                result.setPixel(x, y, ARGB.scaleRGB(imageColor, scale));
            }
        }

        return result;
    }

    // Maps a 3-bit grayscale pixel to a color from the 8x1 color palette
    public record GrayscaleColorer(NativeImage palette) implements IntUnaryOperator {
        @Override
        public int applyAsInt(int shapePixel) {
            int intensity = ARGB.red(shapePixel);
            int alphaMask = shapePixel | 0x00FFFFFF;
            int index = (0xFF - intensity) / 0x24;
            int color = palette.getPixel(index, 0);
            return color & alphaMask;
        }
    }
}
