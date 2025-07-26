package com.masterworks.masterworks.client;

import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import com.masterworks.masterworks.Masterworks;
import com.masterworks.masterworks.item.ToolPartItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.Vertex;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.DisplayRenderer.ItemDisplayRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState.FoilType;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ToolPartSpecialRenderer implements SpecialModelRenderer<ToolPartItem.Construction> {

    public record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<ToolPartSpecialRenderer.Unbaked> MAP_CODEC =
                MapCodec.unit(ToolPartSpecialRenderer.Unbaked::new);

        @Override
        public MapCodec<ToolPartSpecialRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        @Nonnull
        public SpecialModelRenderer<?> bake(@Nonnull EntityModelSet modelSet) {
            Masterworks.LOGGER.debug("Baking ToolPartSpecialRenderer");
            return new ToolPartSpecialRenderer();
        }
    }

    @Override
    @Nullable
    public ToolPartItem.Construction extractArgument(@Nullable ItemStack stack) {
        return ToolPartItem.getConstruction(stack);
    }

    @Override
    public void getExtents(@Nonnull Set<Vector3f> output) {
        Vertex vertex = new Vertex(new Vector3f(), 0, 0);
        float x = vertex.pos().x() / 16.0F;
        float y = vertex.pos().y() / 16.0F;
        float z = vertex.pos().z() / 16.0F;
        Vector3f extent = new PoseStack.Pose().pose().transformPosition(x, y, z, new Vector3f());
        output.add(extent);
    }

    @Override
    public void render(@Nullable ToolPartItem.Construction construction,
            @Nonnull ItemDisplayContext displayContext, @Nonnull PoseStack poseStack,
            @Nonnull MultiBufferSource bufferSource, int light, int overlay, boolean hasFoil) {
        if (construction == null) {
            return;
        }

        ResourceLocation texture = ToolPartTextureManager.getInstance().get(construction);

        poseStack.pushPose();

        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.translate(1, 1, 1);

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(texture));

        renderCustomGeometry(poseStack, vertexConsumer, light, overlay);
        if (hasFoil) {
            renderFoilEffect(poseStack, bufferSource, light, overlay);
        }
        poseStack.popPose();
    }

    private static void renderCustomGeometry(PoseStack poseStack, VertexConsumer vertexConsumer,
            int light, int overlay) {
        // Render a custom quad/model using vertices
        PoseStack.Pose pose = poseStack.last();
        Matrix4f positionMatrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();

        // Define vertices for a simple quad
        float size = 1f;
        Vector3f normal = new Vector3f(0, 0, 1);
        normal.mul(normalMatrix);

        // Front face quad
        addVertex(vertexConsumer, positionMatrix, normalMatrix, -size, -size, 0, 0, 1, normal,
                light, overlay);
        addVertex(vertexConsumer, positionMatrix, normalMatrix, size, -size, 0, 1, 1, normal, light,
                overlay);
        addVertex(vertexConsumer, positionMatrix, normalMatrix, size, size, 0, 1, 0, normal, light,
                overlay);
        addVertex(vertexConsumer, positionMatrix, normalMatrix, -size, size, 0, 0, 0, normal, light,
                overlay);
    }

    private static void addVertex(VertexConsumer consumer, Matrix4f positionMatrix,
            Matrix3f normalMatrix, float x, float y, float z, float u, float v, Vector3f normal,
            int light, int overlay) {
        consumer.addVertex(positionMatrix, x, y, z).setColor(255, 255, 255, 255).setUv(u, v)
                .setOverlay(overlay).setLight(light).setNormal(normal.x(), normal.y(), normal.z());
        // maybe setNormal with pose
    }

    private static void renderFoilEffect(PoseStack poseStack, MultiBufferSource bufferSource,
            int light, int overlay) {
        VertexConsumer foilConsumer = bufferSource.getBuffer(RenderType.entityGlint());
        renderCustomGeometry(poseStack, foilConsumer, light, overlay);
    }
}
