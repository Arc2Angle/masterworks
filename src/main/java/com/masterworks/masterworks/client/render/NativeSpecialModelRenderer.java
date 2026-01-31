package com.masterworks.masterworks.client.render;

import com.masterworks.masterworks.client.render.geometry.Voxels;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3f;

public abstract class NativeSpecialModelRenderer<T> implements SpecialModelRenderer<T> {

    @Nullable
    protected abstract NativeImage getImage(@Nullable T argument);

    @Override
    public void getExtents(@Nonnull Set<Vector3f> output) {
        Vector3f extent = new PoseStack.Pose().pose().transformPosition(new Vector3f(0f, 0f, 0f));
        output.add(extent);
    }

    private static final float THICKNESS = 0.0625f;

    @Override
    public void submit(
            @Nullable T argument,
            @Nonnull ItemDisplayContext displayContext,
            @Nonnull PoseStack poseStack,
            @Nonnull SubmitNodeCollector nodeCollector,
            int packedLight,
            int packedOverlay,
            boolean hasFoil,
            int outlineColor) {

        NativeImage image = getImage(argument);
        if (image == null) {
            return;
        }

        SubmitNodeCollector.CustomGeometryRenderer renderer =
                Voxels.fromNativeImage(image, THICKNESS).renderer(packedLight, packedOverlay);

        poseStack.pushPose();
        poseStack.translate(0, 1, 0);
        poseStack.scale(1, -1, 1);

        nodeCollector.submitCustomGeometry(poseStack, RenderType.debugQuads(), renderer);

        if (hasFoil) {
            nodeCollector.submitCustomGeometry(poseStack, RenderType.entityGlint(), renderer);
        }

        poseStack.popPose();
    }
}
