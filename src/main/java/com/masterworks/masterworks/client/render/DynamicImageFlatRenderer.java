package com.masterworks.masterworks.client.render;

import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart.Vertex;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;

public abstract class DynamicImageFlatRenderer<T> implements SpecialModelRenderer<T> {

    protected abstract NativeImage getImage(@Nonnull T argument);

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
    public void render(@Nullable T argument, @Nonnull ItemDisplayContext displayContext,
            @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource bufferSource, int light,
            int overlay, boolean hasFoil) {
        if (argument == null) {
            return;
        }

        NativeImage image = getImage(argument);

        poseStack.pushPose();
        poseStack.scale(0.5f, 0.5f, 0.5f);
        poseStack.translate(1, 1, 1);

        renderNativeImageFlat(image, poseStack, bufferSource.getBuffer(RenderType.debugQuads()),
                light, overlay);

        if (hasFoil) {
            renderNativeImageFlat(image, poseStack,
                    bufferSource.getBuffer(RenderType.entityGlint()), light, overlay);
        }

        poseStack.popPose();
    }

    private static final float THICKNESS = 1f / 16f;

    private static void renderNativeImageFlat(NativeImage image, PoseStack poseStack,
            VertexConsumer vertexConsumer, int light, int overlay) {

        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels = image.getPixels(); // returns ARGB

        VertexConsumerWrapper consumer =
                new VertexConsumerWrapper(vertexConsumer, poseStack, light, overlay);

        for (int i = 0; i < pixels.length; i++) {
            int color = pixels[i];

            if ((color & 0xFF000000) == 0) {
                continue;
            }

            float xp = i % width;
            float yp = height - i / width - 1;

            float x0 = 2 * xp / width - 1;
            float y0 = 2 * yp / height - 1;
            float x1 = 2 * (xp + 1) / width - 1;
            float y1 = 2 * (yp + 1) / height - 1;

            consumer.addVertex(x0, y0, -THICKNESS, color, Direction.BACK);
            consumer.addVertex(x1, y0, -THICKNESS, color, Direction.BACK);
            consumer.addVertex(x1, y1, -THICKNESS, color, Direction.BACK);
            consumer.addVertex(x0, y1, -THICKNESS, color, Direction.BACK);

            consumer.addVertex(x0, y0, THICKNESS, color, Direction.FRONT);
            consumer.addVertex(x1, y0, THICKNESS, color, Direction.FRONT);
            consumer.addVertex(x1, y1, THICKNESS, color, Direction.FRONT);
            consumer.addVertex(x0, y1, THICKNESS, color, Direction.FRONT);

            consumer.addVertex(x0, y0, -THICKNESS, color, Direction.LEFT);
            consumer.addVertex(x0, y0, THICKNESS, color, Direction.LEFT);
            consumer.addVertex(x0, y1, THICKNESS, color, Direction.LEFT);
            consumer.addVertex(x0, y1, -THICKNESS, color, Direction.LEFT);

            consumer.addVertex(x1, y0, THICKNESS, color, Direction.RIGHT);
            consumer.addVertex(x1, y0, -THICKNESS, color, Direction.RIGHT);
            consumer.addVertex(x1, y1, -THICKNESS, color, Direction.RIGHT);
            consumer.addVertex(x1, y1, THICKNESS, color, Direction.RIGHT);

            consumer.addVertex(x0, y0, -THICKNESS, color, Direction.BOTTOM);
            consumer.addVertex(x1, y0, -THICKNESS, color, Direction.BOTTOM);
            consumer.addVertex(x1, y0, THICKNESS, color, Direction.BOTTOM);
            consumer.addVertex(x0, y0, THICKNESS, color, Direction.BOTTOM);

            consumer.addVertex(x0, y1, THICKNESS, color, Direction.TOP);
            consumer.addVertex(x1, y1, THICKNESS, color, Direction.TOP);
            consumer.addVertex(x1, y1, -THICKNESS, color, Direction.TOP);
            consumer.addVertex(x0, y1, -THICKNESS, color, Direction.TOP);
        }
    }


    private enum Direction {
        BACK(new Vector3f(0, 0, -1)), FRONT(new Vector3f(0, 0, 1)), LEFT(
                new Vector3f(-1, 0, 0)), RIGHT(new Vector3f(1, 0, 0)), BOTTOM(
                        new Vector3f(0, -1, 0)), TOP(new Vector3f(0, 1, 0));

        public final Vector3f normal;

        Direction(Vector3f normal) {
            this.normal = normal;
        }
    }

    private static class VertexConsumerWrapper {
        private final VertexConsumer delegate;
        private final PoseStack.Pose pose;
        private final Matrix4f positionMatrix;
        private final Matrix3f normalMatrix;
        private final int light;
        private final int overlay;

        public VertexConsumerWrapper(VertexConsumer delegate, PoseStack poseStack, int light,
                int overlay) {
            this.delegate = delegate;
            this.pose = poseStack.last();
            this.positionMatrix = poseStack.last().pose();
            this.normalMatrix = poseStack.last().normal();
            this.light = light;
            this.overlay = overlay;
        }

        public void addVertex(float x, float y, float z, int color, Direction direction) {
            delegate.addVertex(positionMatrix, x, y, z).setColor(color).setUv(0, 0)
                    .setOverlay(overlay).setLight(light)
                    .setNormal(pose, direction.normal.mul(normalMatrix));
        }
    }
}
