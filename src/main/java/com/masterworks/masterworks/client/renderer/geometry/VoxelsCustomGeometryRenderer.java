package com.masterworks.masterworks.client.renderer.geometry;

import com.masterworks.masterworks.util.vox.Voxels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.util.ARGB;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;

public record VoxelsCustomGeometryRenderer(Voxels voxels, int light, int overlay)
        implements SubmitNodeCollector.CustomGeometryRenderer {

    @Override
    public void render(PoseStack.Pose pose, VertexConsumer consumer) {
        Vector3f left = new Vector3f(-1, 0, 0).mul(pose.normal());
        Vector3f right = new Vector3f(1, 0, 0).mul(pose.normal());
        Vector3f bottom = new Vector3f(0, -1, 0).mul(pose.normal());
        Vector3f top = new Vector3f(0, 1, 0).mul(pose.normal());
        Vector3f back = new Vector3f(0, 0, -1).mul(pose.normal());
        Vector3f front = new Vector3f(0, 0, 1).mul(pose.normal());

        Matrix4f mat = pose.pose();
        Vector3f pos = new Vector3f(); // used as a destination for transformed positions

        int[] colors = voxels.colors();
        Vector3i count = voxels.count();
        Vector3f size = voxels.size();
        Vector3f offset = voxels.offset();

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
                mat.transformPosition(x0, y0, z0, pos);
                consumer.addVertex(pos.x, pos.y, pos.z, color, 0, 0, overlay, light, left.x, left.y, left.z);

                mat.transformPosition(x0, y0, z1, pos);
                consumer.addVertex(pos.x, pos.y, pos.z, color, 0, 0, overlay, light, left.x, left.y, left.z);

                mat.transformPosition(x0, y1, z1, pos);
                consumer.addVertex(pos.x, pos.y, pos.z, color, 0, 0, overlay, light, left.x, left.y, left.z);

                mat.transformPosition(x0, y1, z0, pos);
                consumer.addVertex(pos.x, pos.y, pos.z, color, 0, 0, overlay, light, left.x, left.y, left.z);
            }

            if (xi == count.x - 1 || ARGB.alpha(colors[i + 1]) != 0xFF) {
                mat.transformPosition(x1, y0, z1, pos);
                consumer.addVertex(pos.x, pos.y, pos.z, color, 0, 0, overlay, light, right.x, right.y, right.z);

                mat.transformPosition(x1, y0, z0, pos);
                consumer.addVertex(pos.x, pos.y, pos.z, color, 0, 0, overlay, light, right.x, right.y, right.z);

                mat.transformPosition(x1, y1, z0, pos);
                consumer.addVertex(pos.x, pos.y, pos.z, color, 0, 0, overlay, light, right.x, right.y, right.z);

                mat.transformPosition(x1, y1, z1, pos);
                consumer.addVertex(pos.x, pos.y, pos.z, color, 0, 0, overlay, light, right.x, right.y, right.z);
            }

            if (yi == 0 || ARGB.alpha(colors[i - count.x]) != 0xFF) {
                mat.transformPosition(x0, y0, z0, pos);
                consumer.addVertex(pos.x, pos.y, pos.z, color, 0, 0, overlay, light, bottom.x, bottom.y, bottom.z);

                mat.transformPosition(x1, y0, z0, pos);
                consumer.addVertex(pos.x, pos.y, pos.z, color, 0, 0, overlay, light, bottom.x, bottom.y, bottom.z);

                mat.transformPosition(x1, y0, z1, pos);
                consumer.addVertex(pos.x, pos.y, pos.z, color, 0, 0, overlay, light, bottom.x, bottom.y, bottom.z);

                mat.transformPosition(x0, y0, z1, pos);
                consumer.addVertex(pos.x, pos.y, pos.z, color, 0, 0, overlay, light, bottom.x, bottom.y, bottom.z);
            }

            if (yi == count.y - 1 || ARGB.alpha(colors[i + count.x]) != 0xFF) {
                mat.transformPosition(x0, y1, z1, pos);
                consumer.addVertex(pos.x, pos.y, pos.z, color, 0, 0, overlay, light, top.x, top.y, top.z);

                mat.transformPosition(x1, y1, z1, pos);
                consumer.addVertex(pos.x, pos.y, pos.z, color, 0, 0, overlay, light, top.x, top.y, top.z);

                mat.transformPosition(x1, y1, z0, pos);
                consumer.addVertex(pos.x, pos.y, pos.z, color, 0, 0, overlay, light, top.x, top.y, top.z);

                mat.transformPosition(x0, y1, z0, pos);
                consumer.addVertex(pos.x, pos.y, pos.z, color, 0, 0, overlay, light, top.x, top.y, top.z);
            }

            if (zi == 0 || ARGB.alpha(colors[i - count.x * count.y]) != 0xFF) {
                mat.transformPosition(x0, y0, z0, pos);
                consumer.addVertex(pos.x, pos.y, pos.z, color, 0, 0, overlay, light, back.x, back.y, back.z);

                mat.transformPosition(x1, y0, z0, pos);
                consumer.addVertex(pos.x, pos.y, pos.z, color, 0, 0, overlay, light, back.x, back.y, back.z);

                mat.transformPosition(x1, y1, z0, pos);
                consumer.addVertex(pos.x, pos.y, pos.z, color, 0, 0, overlay, light, back.x, back.y, back.z);

                mat.transformPosition(x0, y1, z0, pos);
                consumer.addVertex(pos.x, pos.y, pos.z, color, 0, 0, overlay, light, back.x, back.y, back.z);
            }

            if (zi == count.z - 1 || ARGB.alpha(colors[i + count.x * count.y]) != 0xFF) {
                mat.transformPosition(x0, y0, z1, pos);
                consumer.addVertex(pos.x, pos.y, pos.z, color, 0, 0, overlay, light, front.x, front.y, front.z);

                mat.transformPosition(x1, y0, z1, pos);
                consumer.addVertex(pos.x, pos.y, pos.z, color, 0, 0, overlay, light, front.x, front.y, front.z);

                mat.transformPosition(x1, y1, z1, pos);
                consumer.addVertex(pos.x, pos.y, pos.z, color, 0, 0, overlay, light, front.x, front.y, front.z);

                mat.transformPosition(x0, y1, z1, pos);
                consumer.addVertex(pos.x, pos.y, pos.z, color, 0, 0, overlay, light, front.x, front.y, front.z);
            }
        }
    }
}
