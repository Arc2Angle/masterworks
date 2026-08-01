package com.masterworks.masterworks.client.part.wavefront;

import net.minecraft.util.Mth;
import org.joml.Vector2f;

public sealed interface WavefrontMaterial {
    int sample(float u, float v);

    record DiffuseTexture(int width, int height, int[] pixels) implements WavefrontMaterial {
        @Override
        public int sample(float u, float v) {
            int px = Mth.clamp((int) Math.floor(u * width), 0, width - 1);
            int py = Mth.clamp((int) Math.floor((1 - v) * height), 0, height - 1);
            return pixels[py * width + px];
        }
    }

    default int sampleCenter(Vector2f[] uvs) {
        float u = 0;
        float v = 0;

        for (Vector2f uv : uvs) {
            u += uv.x;
            v += uv.y;
        }

        return sample(u / uvs.length, v / uvs.length);
    }
}
