package com.masterworks.masterworks.client.render;

import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemDisplayContext;

public abstract class NativeSpecialRenderer<T> implements SpecialModelRenderer<T> {

    /**
     * Represents the pixels to be rendered.
     * 
     * @param colors are the pixel colors in ARGB format, interpreted as [width][height][depth].
     *        width and height are the x-axis and y-axis in GUI rendering, while depth is the item
     *        model's depth when rendered in world.
     * @param count is the number of pixels in each dimension.
     * @param size is the size of the entire rendered image, measured in blocks.
     * @param offset is the offset to apply to the rendered image, typically used to center the
     *        image.
     */
    protected record Pixels(int[] colors, Vector3i count, Vector3f size, Vector3f offset) {
    }

    @Nullable
    protected abstract Pixels getPixels(@Nullable T argument);

    @Override
    public void getExtents(@Nonnull Set<Vector3f> output) {
        Vector3f extent = new PoseStack.Pose().pose().transformPosition(new Vector3f(0f, 0f, 0f));
        output.add(extent);
    }

    @Override
    public void render(@Nullable T argument, @Nonnull ItemDisplayContext displayContext,
            @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource bufferSource, int light,
            int overlay, boolean hasFoil) {

        Pixels pixels = getPixels(argument);
        if (pixels == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0, 1, 0);
        poseStack.scale(1, -1, 1);

        renderNative(pixels, bufferSource.getBuffer(RenderType.debugQuads()), poseStack, light,
                overlay);

        if (hasFoil) {
            renderNative(pixels, bufferSource.getBuffer(RenderType.entityGlint()), poseStack, light,
                    overlay);
        }

        poseStack.popPose();
    }

    private static void renderNative(Pixels pixels, VertexConsumer vertexConsumer,
            PoseStack poseStack, int light, int overlay) {

        VertexConsumerWrapper consumer =
                new VertexConsumerWrapper(vertexConsumer, poseStack, light, overlay);

        for (int i = 0; i < pixels.colors.length; i++) {
            int color = pixels.colors[i];

            if (ARGB.alpha(color) == 0) {
                continue;
            }

            float xi = i % pixels.count.x;
            float yi = (i % (pixels.count.x * pixels.count.y)) / pixels.count.x;
            float zi = i / (pixels.count.x * pixels.count.y);

            float x0 = pixels.size.x * xi / pixels.count.x + pixels.offset.x;
            float y0 = pixels.size.y * yi / pixels.count.y + pixels.offset.y;
            float z0 = pixels.size.z * zi / pixels.count.z + pixels.offset.z;
            float x1 = pixels.size.x * (xi + 1) / pixels.count.x + pixels.offset.x;
            float y1 = pixels.size.y * (yi + 1) / pixels.count.y + pixels.offset.y;
            float z1 = pixels.size.z * (zi + 1) / pixels.count.z + pixels.offset.z;

            if (xi == 0 || ARGB.alpha(pixels.colors[i - 1]) != 0xFF) {
                consumer.addVertex(x0, y0, z0, color, Direction.LEFT);
                consumer.addVertex(x0, y0, z1, color, Direction.LEFT);
                consumer.addVertex(x0, y1, z1, color, Direction.LEFT);
                consumer.addVertex(x0, y1, z0, color, Direction.LEFT);
            }

            if (xi == pixels.count.x - 1 || ARGB.alpha(pixels.colors[i + 1]) != 0xFF) {
                consumer.addVertex(x1, y0, z1, color, Direction.RIGHT);
                consumer.addVertex(x1, y0, z0, color, Direction.RIGHT);
                consumer.addVertex(x1, y1, z0, color, Direction.RIGHT);
                consumer.addVertex(x1, y1, z1, color, Direction.RIGHT);
            }

            if (yi == 0 || ARGB.alpha(pixels.colors[i - pixels.count.x]) != 0xFF) {
                consumer.addVertex(x0, y0, z0, color, Direction.BOTTOM);
                consumer.addVertex(x1, y0, z0, color, Direction.BOTTOM);
                consumer.addVertex(x1, y0, z1, color, Direction.BOTTOM);
                consumer.addVertex(x0, y0, z1, color, Direction.BOTTOM);
            }

            if (yi == pixels.count.y - 1 || ARGB.alpha(pixels.colors[i + pixels.count.x]) != 0xFF) {
                consumer.addVertex(x0, y1, z1, color, Direction.TOP);
                consumer.addVertex(x1, y1, z1, color, Direction.TOP);
                consumer.addVertex(x1, y1, z0, color, Direction.TOP);
                consumer.addVertex(x0, y1, z0, color, Direction.TOP);
            }

            if (zi == 0 || ARGB.alpha(pixels.colors[i - pixels.count.x * pixels.count.y]) != 0xFF) {
                consumer.addVertex(x0, y0, z0, color, Direction.BACK);
                consumer.addVertex(x1, y0, z0, color, Direction.BACK);
                consumer.addVertex(x1, y1, z0, color, Direction.BACK);
                consumer.addVertex(x0, y1, z0, color, Direction.BACK);
            }

            if (zi == pixels.count.z - 1
                    || ARGB.alpha(pixels.colors[i + pixels.count.x * pixels.count.y]) != 0xFF) {
                consumer.addVertex(x0, y0, z1, color, Direction.FRONT);
                consumer.addVertex(x1, y0, z1, color, Direction.FRONT);
                consumer.addVertex(x1, y1, z1, color, Direction.FRONT);
                consumer.addVertex(x0, y1, z1, color, Direction.FRONT);
            }
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
