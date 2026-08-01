package com.masterworks.masterworks.client.part.submit;

import com.masterworks.masterworks.client.part.PartSubmitter;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import org.joml.Vector3f;

public record NaiveGeometryPartSubmitter(Vector3f[][] positions, int[] colors, Vector3f[][] normals)
        implements PartSubmitter {

    @Override
    public void submit(
            SubmitNodeCollector collector, Poser poser, PoseStack stack, int packedLight, int packedOverlay) {
        collector.submitCustomGeometry(
                stack,
                RenderTypes.debugQuads(),
                new CustomGeometryRenderer(positions, colors, normals, packedLight, packedOverlay));
    }

    private record CustomGeometryRenderer(
            Vector3f[][] positions, int[] colors, Vector3f[][] normals, int packedLight, int packedOverlay)
            implements SubmitNodeCollector.CustomGeometryRenderer {
        @Override
        public void render(PoseStack.Pose pose, VertexConsumer consumer) {
            if (positions.length != colors.length || positions.length != normals.length) {
                throw new IllegalArgumentException("Positions, colors, and normals arrays must have the same length");
            }

            for (int i = 0; i < positions.length; i++) {
                if (positions[i].length != normals[i].length) {
                    throw new IllegalArgumentException(
                            "Positions and normals arrays must have the same length for each face");
                }

                for (int j = 0; j < positions[i].length; j++) {
                    Vector3f position = positions[i][j];
                    Vector3f normal = normals[i][j];
                    int color = colors[i];

                    consumer.addVertex(pose, position.x, position.y, position.z)
                            .setColor(color)
                            .setUv(0, 0)
                            .setOverlay(packedOverlay)
                            .setLight(packedLight)
                            .setNormal(normal.x, normal.y, normal.z);
                }
            }
        }
    }
}
