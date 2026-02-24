package com.masterworks.masterworks.client.renderer.model;

import com.masterworks.masterworks.client.renderer.geometry.VoxelsCustomGeometryRenderer;
import com.masterworks.masterworks.util.vox.Voxels;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3f;

public abstract class VoxelsSpecialModelRenderer<T> implements SpecialModelRenderer<T> {

    @Nullable
    protected abstract Voxels getVoxels(@Nullable T argument);

    @Override
    public void getExtents(@Nonnull Set<Vector3f> output) {
        Vector3f extent = new PoseStack.Pose().pose().transformPosition(new Vector3f(0f, 0f, 0f));
        output.add(extent);
    }

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

        Voxels voxels = getVoxels(argument);
        if (voxels == null) {
            return;
        }

        SubmitNodeCollector.CustomGeometryRenderer renderer =
                new VoxelsCustomGeometryRenderer(voxels, packedLight, packedOverlay);

        poseStack.pushPose();

        nodeCollector.submitCustomGeometry(poseStack, RenderType.debugQuads(), renderer);
        if (hasFoil) {
            nodeCollector.submitCustomGeometry(poseStack, RenderType.entityGlint(), renderer);
        }

        poseStack.popPose();
    }
}
