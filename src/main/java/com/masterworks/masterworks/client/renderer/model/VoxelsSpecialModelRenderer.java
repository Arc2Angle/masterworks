package com.masterworks.masterworks.client.renderer.model;

import com.masterworks.masterworks.client.renderer.geometry.VoxelsCustomGeometryRenderer;
import com.masterworks.masterworks.util.vox.Voxels;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public abstract class VoxelsSpecialModelRenderer<T> implements SpecialModelRenderer<T> {

    @Nullable
    protected abstract Voxels getVoxels(@Nullable T argument);

    @Override
    public void getExtents(@Nonnull Consumer<Vector3fc> output) {
        Vector3fc extent = new PoseStack.Pose().pose().transformPosition(new Vector3f(0f, 0f, 0f));
        output.accept(extent);
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

        nodeCollector.submitCustomGeometry(poseStack, RenderTypes.debugQuads(), renderer);
        if (hasFoil) {
            nodeCollector.submitCustomGeometry(poseStack, RenderTypes.entityGlint(), renderer);
        }

        poseStack.popPose();
    }
}
