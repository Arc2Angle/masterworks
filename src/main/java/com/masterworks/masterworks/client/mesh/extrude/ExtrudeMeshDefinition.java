package com.masterworks.masterworks.client.mesh.extrude;

import com.masterworks.masterworks.client.MasterworksMeshDefinitionTypes;
import com.masterworks.masterworks.client.mesh.Mesh;
import com.masterworks.masterworks.client.mesh.MeshDefinition;
import com.masterworks.masterworks.client.mesh.core.DiffuseMesh;
import com.masterworks.masterworks.util.Vertex;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ARGB;
import org.joml.Vector2f;
import org.joml.Vector3f;

public record ExtrudeMeshDefinition(Identifier diffuse, float depth) implements MeshDefinition {
    public static final MapCodec<ExtrudeMeshDefinition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Identifier.CODEC.fieldOf("diffuse").forGetter(ExtrudeMeshDefinition::diffuse),
                    Codec.FLOAT.optionalFieldOf("depth", 0.5f).forGetter(ExtrudeMeshDefinition::depth))
            .apply(instance, ExtrudeMeshDefinition::new));

    @Override
    public Type<?> type() {
        return MasterworksMeshDefinitionTypes.EXTRUDE.get();
    }

    private static final FileToIdConverter DIFFUSES = new FileToIdConverter("textures/mesh", ".png");

    private static final Vector3f VOXEL_SIZE = new Vector3f(1 / 16f);
    private static final Vector3f // normals
            LEFT = new Vector3f(1, 0, 0),
            RIGHT = new Vector3f(-1, 0, 0),
            UP = new Vector3f(0, 1, 0),
            DOWN = new Vector3f(0, -1, 0),
            FRONT = new Vector3f(0, 0, 1),
            BACK = new Vector3f(0, 0, -1);

    @Override
    public Mesh bake(ResourceManager resourceManager) {
        Identifier diffuseLocation = DIFFUSES.idToFile(diffuse);

        int width;
        int height;
        int[] alphas;

        try (InputStream stream = resourceManager.open(diffuseLocation);
                NativeImage image = NativeImage.read(stream)) {
            width = image.getWidth();
            height = image.getHeight();
            alphas = new int[width * height];

            int[] pixels = image.getPixels();
            for (int i = 0; i < pixels.length; i++) {
                alphas[i] = ARGB.alpha(pixels[i]);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read diffuse", e);
        }

        List<Vertex> vertices = new ArrayList<>();

        float iwidth = 1.0f / width;
        float iheight = 1.0f / height;
        float zFront = depth * VOXEL_SIZE.z();
        float zBack = -zFront;

        vertices.add(new Vertex(new Vector3f(0, 0, zFront), new Vector2f(0, 1), FRONT));
        vertices.add(new Vertex(new Vector3f(1, 0, zFront), new Vector2f(1, 1), FRONT));
        vertices.add(new Vertex(new Vector3f(1, 1, zFront), new Vector2f(1, 0), FRONT));
        vertices.add(new Vertex(new Vector3f(0, 1, zFront), new Vector2f(0, 0), FRONT));

        vertices.add(new Vertex(new Vector3f(0, 0, zBack), new Vector2f(0, 1), BACK));
        vertices.add(new Vertex(new Vector3f(1, 0, zBack), new Vector2f(1, 1), BACK));
        vertices.add(new Vertex(new Vector3f(1, 1, zBack), new Vector2f(1, 0), BACK));
        vertices.add(new Vertex(new Vector3f(0, 1, zBack), new Vector2f(0, 0), BACK));

        for (int i = 0; i < alphas.length; i++) {
            int alpha = alphas[i];
            if (alpha == 0) {
                continue;
            }

            int xi = i % width;
            int yi = i / width;

            float u0 = xi * iwidth;
            float u1 = u0 + iwidth;
            float v0 = yi * iheight;
            float v1 = v0 + iheight;

            Vector2f uv0 = new Vector2f(u0, v0);
            Vector2f uv1 = new Vector2f(u1, v0);
            Vector2f uv2 = new Vector2f(u1, v1);
            Vector2f uv3 = new Vector2f(u0, v1);

            float xLeft = xi * VOXEL_SIZE.x();
            float xRight = xLeft + VOXEL_SIZE.x();
            float yUp = (height - yi) * VOXEL_SIZE.y();
            float yDown = yUp - VOXEL_SIZE.y();

            Vector3f leftUpFront = new Vector3f(xLeft, yUp, zFront);
            Vector3f rightUpFront = new Vector3f(xRight, yUp, zFront);
            Vector3f rightDownFront = new Vector3f(xRight, yDown, zFront);
            Vector3f leftDownFront = new Vector3f(xLeft, yDown, zFront);
            Vector3f leftUpBack = new Vector3f(xLeft, yUp, zBack);
            Vector3f rightUpBack = new Vector3f(xRight, yUp, zBack);
            Vector3f rightDownBack = new Vector3f(xRight, yDown, zBack);
            Vector3f leftDownBack = new Vector3f(xLeft, yDown, zBack);

            if (xi == 0 || alphas[i - 1] == 0) {
                vertices.add(new Vertex(leftUpFront, uv0, LEFT));
                vertices.add(new Vertex(leftDownFront, uv1, LEFT));
                vertices.add(new Vertex(leftDownBack, uv2, LEFT));
                vertices.add(new Vertex(leftUpBack, uv3, LEFT));
            }

            if (xi == width - 1 || alphas[i + 1] == 0) {
                vertices.add(new Vertex(rightUpFront, uv0, RIGHT));
                vertices.add(new Vertex(rightUpBack, uv1, RIGHT));
                vertices.add(new Vertex(rightDownBack, uv2, RIGHT));
                vertices.add(new Vertex(rightDownFront, uv3, RIGHT));
            }

            if (yi == 0 || alphas[i - width] == 0) {
                vertices.add(new Vertex(leftUpFront, uv0, UP));
                vertices.add(new Vertex(leftUpBack, uv1, UP));
                vertices.add(new Vertex(rightUpBack, uv2, UP));
                vertices.add(new Vertex(rightUpFront, uv3, UP));
            }

            if (yi == height - 1 || alphas[i + width] == 0) {
                vertices.add(new Vertex(rightDownFront, uv0, DOWN));
                vertices.add(new Vertex(leftDownFront, uv1, DOWN));
                vertices.add(new Vertex(leftDownBack, uv2, DOWN));
                vertices.add(new Vertex(rightDownBack, uv3, DOWN));
            }
        }

        return new DiffuseMesh(diffuseLocation, vertices.toArray(new Vertex[0]));
    }
}
