package com.masterworks.masterworks.client.renderer.model;

import com.masterworks.masterworks.util.vox.Voxels;
import com.mojang.blaze3d.platform.NativeImage;
import javax.annotation.Nullable;
import org.joml.Vector3f;

public abstract class NativeImageSpecialModelRenderer<T> extends VoxelsSpecialModelRenderer<T> {

    @Nullable
    protected abstract NativeImage getImage(@Nullable T argument);

    public static final Vector3f MODEL_SIZE = new Vector3f(1f, 1f, 1f / 16f);
    public static final Vector3f MODEL_OFFSET = new Vector3f(0f, 0f, 0.5f - 1f / 32f);

    @Override
    protected Voxels getVoxels(T argument) {
        NativeImage image = getImage(argument);
        if (image == null) {
            return null;
        }

        return Voxels.copyFromImage(image, MODEL_SIZE, MODEL_OFFSET);
    }
}
