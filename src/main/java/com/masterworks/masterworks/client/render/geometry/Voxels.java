package com.masterworks.masterworks.client.render.geometry;

import javax.annotation.Nonnull;
import org.joml.Vector3f;
import org.joml.Vector3i;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.util.ARGB;

public record Voxels(int[] colors, Vector3i count, Vector3f size, Vector3f offset) {

    private enum Direction {
        BACK(new Vector3f(0, 0, -1)), FRONT(new Vector3f(0, 0, 1)), LEFT(
                new Vector3f(-1, 0, 0)), RIGHT(new Vector3f(1, 0, 0)), BOTTOM(
                        new Vector3f(0, -1, 0)), TOP(new Vector3f(0, 1, 0));

        public final Vector3f normal;

        Direction(Vector3f normal) {
            this.normal = normal;
        }
    }

    public SubmitNodeCollector.CustomGeometryRenderer renderer(int packedLight, int packedOverlay) {
        return new SubmitNodeCollector.CustomGeometryRenderer() {
            @Override
            public void render(PoseStack.Pose pose, VertexConsumer consumer) {
                var wrapper = new Object() {
                    public void addVertex(float x, float y, float z, int color,
                            Direction direction) {
                        consumer.addVertex(pose.pose(), x, y, z).setColor(color).setUv(0, 0)
                                .setOverlay(packedOverlay).setLight(packedLight)
                                .setNormal(pose, direction.normal.mul(pose.normal()));
                    }
                };

                for (int i = 0; i < colors.length; i++) {
                    int color = colors[i];

                    if (ARGB.alpha(color) == 0) {
                        continue;
                    }

                    float xi = i % count.x;
                    float yi = (i % (count.x * count.y)) / count.x;
                    float zi = i / (count.x * count.y);

                    float x0 = size.x * xi / count.x + offset.x;
                    float y0 = size.y * yi / count.y + offset.y;
                    float z0 = size.z * zi / count.z + offset.z;
                    float x1 = size.x * (xi + 1) / count.x + offset.x;
                    float y1 = size.y * (yi + 1) / count.y + offset.y;
                    float z1 = size.z * (zi + 1) / count.z + offset.z;

                    if (xi == 0 || ARGB.alpha(colors[i - 1]) != 0xFF) {
                        wrapper.addVertex(x0, y0, z0, color, Direction.LEFT);
                        wrapper.addVertex(x0, y0, z1, color, Direction.LEFT);
                        wrapper.addVertex(x0, y1, z1, color, Direction.LEFT);
                        wrapper.addVertex(x0, y1, z0, color, Direction.LEFT);
                    }

                    if (xi == count.x - 1 || ARGB.alpha(colors[i + 1]) != 0xFF) {
                        wrapper.addVertex(x1, y0, z1, color, Direction.RIGHT);
                        wrapper.addVertex(x1, y0, z0, color, Direction.RIGHT);
                        wrapper.addVertex(x1, y1, z0, color, Direction.RIGHT);
                        wrapper.addVertex(x1, y1, z1, color, Direction.RIGHT);
                    }

                    if (yi == 0 || ARGB.alpha(colors[i - count.x]) != 0xFF) {
                        wrapper.addVertex(x0, y0, z0, color, Direction.BOTTOM);
                        wrapper.addVertex(x1, y0, z0, color, Direction.BOTTOM);
                        wrapper.addVertex(x1, y0, z1, color, Direction.BOTTOM);
                        wrapper.addVertex(x0, y0, z1, color, Direction.BOTTOM);
                    }

                    if (yi == count.y - 1 || ARGB.alpha(colors[i + count.x]) != 0xFF) {
                        wrapper.addVertex(x0, y1, z1, color, Direction.TOP);
                        wrapper.addVertex(x1, y1, z1, color, Direction.TOP);
                        wrapper.addVertex(x1, y1, z0, color, Direction.TOP);
                        wrapper.addVertex(x0, y1, z0, color, Direction.TOP);
                    }

                    if (zi == 0 || ARGB.alpha(colors[i - count.x * count.y]) != 0xFF) {
                        wrapper.addVertex(x0, y0, z0, color, Direction.BACK);
                        wrapper.addVertex(x1, y0, z0, color, Direction.BACK);
                        wrapper.addVertex(x1, y1, z0, color, Direction.BACK);
                        wrapper.addVertex(x0, y1, z0, color, Direction.BACK);
                    }

                    if (zi == count.z - 1 || ARGB.alpha(colors[i + count.x * count.y]) != 0xFF) {
                        wrapper.addVertex(x0, y0, z1, color, Direction.FRONT);
                        wrapper.addVertex(x1, y0, z1, color, Direction.FRONT);
                        wrapper.addVertex(x1, y1, z1, color, Direction.FRONT);
                        wrapper.addVertex(x0, y1, z1, color, Direction.FRONT);
                    }
                }
            }
        };
    }

    public static Voxels fromNativeImage(@Nonnull NativeImage image, float thickness) {
        int[] colors = image.getPixels();
        Vector3i count = new Vector3i(image.getWidth(), image.getHeight(), 1);
        Vector3f size = new Vector3f(1f, 1f, thickness);
        Vector3f offset = new Vector3f(0f, 0f, 0.5f - thickness / 2f);

        return new Voxels(colors, count, size, offset);
    }
}
