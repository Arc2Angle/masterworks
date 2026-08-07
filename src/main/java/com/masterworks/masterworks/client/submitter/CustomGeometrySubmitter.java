package com.masterworks.masterworks.client.submitter;

import com.masterworks.masterworks.client.mesh.MeshPoser;
import com.masterworks.masterworks.util.Vertex;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;

public record CustomGeometrySubmitter(RenderType renderType, Vertex[] vertices) implements Submitter {

    private static final int UNTINTED = 0xFFFFFFFF;

    private record Renderer(Vertex[] vertices, int packedLight, int packedOverlay)
            implements SubmitNodeCollector.CustomGeometryRenderer {
        @Override
        public void render(PoseStack.Pose pose, VertexConsumer consumer) {
            for (Vertex vertex : vertices) {
                consumer.addVertex(pose, vertex.position().x, vertex.position().y, vertex.position().z)
                        .setColor(UNTINTED)
                        .setUv(vertex.uv().x, vertex.uv().y)
                        .setOverlay(packedOverlay)
                        .setLight(packedLight)
                        .setNormal(vertex.normal().x, vertex.normal().y, vertex.normal().z);
            }
        }
    }

    @Override
    public void submit(
            SubmitNodeCollector collector, MeshPoser poser, PoseStack stack, int packedLight, int packedOverlay) {
        collector.submitCustomGeometry(stack, renderType, new Renderer(vertices, packedLight, packedOverlay));
    }
}
