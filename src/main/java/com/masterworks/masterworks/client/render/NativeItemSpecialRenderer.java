package com.masterworks.masterworks.client.render;

import javax.annotation.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3i;
import com.mojang.blaze3d.platform.NativeImage;

public abstract class NativeItemSpecialRenderer<T> extends NativeSpecialRenderer<T> {

    private static final float THICKNESS = 0.0625f;

    @Nullable
    protected abstract NativeImage getImage(@Nullable T argument);

    @Override
    @Nullable
    protected Pixels getPixels(@Nullable T argument) {
        NativeImage image = getImage(argument);
        if (image == null) {
            return null;
        }

        return new Pixels(image.getPixels(), new Vector3i(image.getWidth(), image.getHeight(), 1),
                new Vector3f(1f, 1f, THICKNESS), new Vector3f(0f, 0f, 0.5f - THICKNESS / 2f));
    }

}
