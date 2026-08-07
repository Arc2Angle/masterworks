package com.masterworks.masterworks.client.mesh.displace;

import com.masterworks.masterworks.client.MasterworksMeshDefinitionTypes;
import com.masterworks.masterworks.client.mesh.Mesh;
import com.masterworks.masterworks.client.mesh.MeshDefinition;
import com.masterworks.masterworks.client.mesh.core.DiffuseMesh;
import com.masterworks.masterworks.util.Color;
import com.masterworks.masterworks.util.Vertex;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import org.joml.Vector2f;
import org.joml.Vector3f;

public record DisplaceMeshDefinition(Identifier diffuse, Optional<Identifier> displace, Identifier index)
        implements MeshDefinition {
    public static final MapCodec<DisplaceMeshDefinition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Identifier.CODEC.fieldOf("diffuse").forGetter(DisplaceMeshDefinition::diffuse),
                    Identifier.CODEC.optionalFieldOf("displace").forGetter(DisplaceMeshDefinition::displace),
                    Identifier.CODEC.fieldOf("index").forGetter(DisplaceMeshDefinition::index))
            .apply(instance, DisplaceMeshDefinition::new));

    @Override
    public Type<?> type() {
        return MasterworksMeshDefinitionTypes.DISPLACE.get();
    }

    private static final FileToIdConverter DIFFUSES = new FileToIdConverter("textures/mesh", ".png");
    private static final FileToIdConverter DISPLACES = new FileToIdConverter("textures/mesh", ".displace.png");
    private static final FileToIdConverter INDICES = new FileToIdConverter("displace", ".index");

    private static final float CUTOUT = -1;
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
        Identifier displaceLocation = DISPLACES.idToFile(displace.orElse(diffuse));
        Identifier indexLocation = INDICES.idToFile(index);

        Map<Color, Float> index;
        int width;
        int height;
        float[] depths;

        try (InputStream displaceStream = resourceManager.open(displaceLocation);
                NativeImage displace = NativeImage.read(displaceStream);
                InputStream indexStream = resourceManager.open(indexLocation);
                BufferedReader indexReader =
                        new BufferedReader(new InputStreamReader(indexStream, StandardCharsets.UTF_8))) {
            index = indexReader
                    .lines()
                    .map(line -> line.split(" "))
                    .filter(parts -> parts.length == 2)
                    .collect(Collectors.toMap(
                            parts -> Color.parseARGB(parts[0].trim()), parts -> Float.parseFloat(parts[1].trim())));

            width = displace.getWidth();
            height = displace.getHeight();
            depths = new float[width * height];
            int[] pixels = displace.getPixelsABGR();

            for (int i = 0; i < pixels.length; i++) {
                depths[i] = index.getOrDefault(Color.fromABGR(pixels[i]), CUTOUT);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to open depthmap", e);
        }

        List<Vertex> vertices = new ArrayList<>();

        float iwidth = 1.0f / width;
        float iheight = 1.0f / height;

        for (int i = 0; i < depths.length; i++) {
            float depth = depths[i];
            if (depth == CUTOUT) {
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
            float zFront = depth * VOXEL_SIZE.z();
            float zBack = -zFront;

            Vector3f leftUpFront = new Vector3f(xLeft, yUp, zFront);
            Vector3f rightUpFront = new Vector3f(xRight, yUp, zFront);
            Vector3f rightDownFront = new Vector3f(xRight, yDown, zFront);
            Vector3f leftDownFront = new Vector3f(xLeft, yDown, zFront);
            Vector3f leftUpBack = new Vector3f(xLeft, yUp, zBack);
            Vector3f rightUpBack = new Vector3f(xRight, yUp, zBack);
            Vector3f rightDownBack = new Vector3f(xRight, yDown, zBack);
            Vector3f leftDownBack = new Vector3f(xLeft, yDown, zBack);

            vertices.add(new Vertex(leftUpFront, uv0, FRONT));
            vertices.add(new Vertex(rightUpFront, uv1, FRONT));
            vertices.add(new Vertex(rightDownFront, uv2, FRONT));
            vertices.add(new Vertex(leftDownFront, uv3, FRONT));

            vertices.add(new Vertex(leftUpBack, uv0, BACK));
            vertices.add(new Vertex(rightUpBack, uv1, BACK));
            vertices.add(new Vertex(rightDownBack, uv2, BACK));
            vertices.add(new Vertex(leftDownBack, uv3, BACK));

            if (xi == 0 || depths[i - 1] < depth) {
                vertices.add(new Vertex(leftUpFront, uv0, LEFT));
                vertices.add(new Vertex(leftDownFront, uv1, LEFT));
                vertices.add(new Vertex(leftDownBack, uv2, LEFT));
                vertices.add(new Vertex(leftUpBack, uv3, LEFT));
            }

            if (xi == width - 1 || depths[i + 1] < depth) {
                vertices.add(new Vertex(rightUpFront, uv0, RIGHT));
                vertices.add(new Vertex(rightUpBack, uv1, RIGHT));
                vertices.add(new Vertex(rightDownBack, uv2, RIGHT));
                vertices.add(new Vertex(rightDownFront, uv3, RIGHT));
            }

            if (yi == 0 || depths[i - width] < depth) {
                vertices.add(new Vertex(leftUpFront, uv0, UP));
                vertices.add(new Vertex(leftUpBack, uv1, UP));
                vertices.add(new Vertex(rightUpBack, uv2, UP));
                vertices.add(new Vertex(rightUpFront, uv3, UP));
            }

            if (yi == height - 1 || depths[i + width] < depth) {
                vertices.add(new Vertex(rightDownFront, uv0, DOWN));
                vertices.add(new Vertex(leftDownFront, uv1, DOWN));
                vertices.add(new Vertex(leftDownBack, uv2, DOWN));
                vertices.add(new Vertex(rightDownBack, uv3, DOWN));
            }
        }

        return new DiffuseMesh(diffuseLocation, vertices.toArray(new Vertex[0]));
    }
}
