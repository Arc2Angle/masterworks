package com.masterworks.masterworks.client.mesh.wavefront;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.masterworks.masterworks.client.mesh.Mesh;
import com.masterworks.masterworks.client.mesh.core.DiffuseMesh;
import com.masterworks.masterworks.client.palette.Palette;
import com.masterworks.masterworks.client.submitter.CompositeSubmitter;
import com.masterworks.masterworks.client.submitter.NamespaceSubmitter;
import com.masterworks.masterworks.client.submitter.Submitter;
import com.masterworks.masterworks.util.Vertex;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import org.joml.Vector2f;
import org.joml.Vector3f;

public record WavefrontObject(Multimap<String, Mesh> meshes) implements Mesh {

    @Override
    public Submitter reify(Palette palette) {
        return new NamespaceSubmitter(meshes.asMap().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> CompositeSubmitter.of(entry.getValue().stream()
                                .map(mesh -> mesh.reify(palette))
                                .toList()))));
    }

    public record Params(ResourceManager resourceManager, String namespace, Path parent, boolean flipV) {}

    public static final WavefrontParser<WavefrontObject, Params, ?> PARSER = new WavefrontParser<>(
            Builder::new,
            Map.of(
                    "mtllib",
                    Builder::parseMaterialLibrary,
                    "o",
                    Builder::parseMesh,
                    "v",
                    Builder::parsePosition,
                    "vt",
                    Builder::parseUV,
                    "vn",
                    Builder::parseNormal,
                    "usemtl",
                    Builder::parseUseMaterial,
                    "f",
                    Builder::parseFace));

    private static final class Builder implements WavefrontParser.Builder<WavefrontObject> {
        final List<Vector3f> positions = new ArrayList<>();
        final List<Vector2f> textures = new ArrayList<>();
        final List<Vector3f> normals = new ArrayList<>();
        final Map<String, WavefrontMaterial> materials = new HashMap<>();
        final Multimap<String, Mesh> meshes = ArrayListMultimap.create();

        String currentMeshName = null;
        List<Vertex> currentVertices = new ArrayList<>();
        WavefrontMaterial currentMaterial = null;

        @Override
        public WavefrontObject build() {
            flushCurrentMesh();
            return new WavefrontObject(meshes);
        }

        /** Parses {@code mtllib <path>} statements, which specify a relative path to a material library. */
        public void parseMaterialLibrary(String content, Params params) {
            String[] paths = split(content);

            if (paths.length != 1) {
                throw new IllegalArgumentException(
                        "mtllib statements specifying multiple libraries are not supported, got " + content);
            }

            Path path = params.parent.resolve(Path.of(paths[0]));
            Identifier location = Identifier.fromNamespaceAndPath(params.namespace, path.toString());

            try (InputStream stream = params.resourceManager.open(location)) {
                materials.putAll(WavefrontMaterialLibrary.PARSER
                        .parse(stream, new WavefrontMaterialLibrary.Params(params.namespace, path.getParent()))
                        .materials());
            } catch (IOException e) {
                throw new RuntimeException("Failed to read material library " + location, e);
            }
        }

        /** Parses {@code o <name>} statements, which define a mesh. */
        public void parseMesh(String content, Params params) {
            if (currentMeshName != null) {
                flushCurrentMesh();
            }

            currentMeshName = content;
        }

        /** Parses {@code v <x> <y> <z>} statements, which define a vertex position. */
        public void parsePosition(String content, Params params) {
            String[] values = split(content);

            if (values.length != 3) {
                throw new IllegalArgumentException("Invalid vector3: " + content);
            }

            positions.add(new Vector3f(
                    Float.parseFloat(values[0]), Float.parseFloat(values[1]), Float.parseFloat(values[2])));
        }

        /** Parses {@code vt <u> <v>} statements, which define a texture coordinate. */
        public void parseUV(String content, Params params) {
            String[] values = split(content);

            if (values.length != 2) {
                throw new IllegalArgumentException("Invalid vector2: " + content);
            }

            float u = Float.parseFloat(values[0]);
            float v = Float.parseFloat(values[1]);

            if (params.flipV) {
                v = 1.0f - v;
            }

            textures.add(new Vector2f(u, v));
        }

        /** Parses {@code vn <x> <y> <z>} statements, which define a vertex normal. */
        public void parseNormal(String content, Params params) {
            String[] values = split(content);

            if (values.length != 3) {
                throw new IllegalArgumentException("Invalid vector3: " + content);
            }

            normals.add(new Vector3f(
                    Float.parseFloat(values[0]), Float.parseFloat(values[1]), Float.parseFloat(values[2])));
        }

        /** Parses {@code usemtl <name>} statements, which specify the name of a material to use for subsequent faces. */
        public void parseUseMaterial(String content, Params params) {
            flushCurrentMesh();

            currentMaterial = materials.get(content);
            if (currentMaterial == null) {
                throw new RuntimeException("Material not found: " + content);
            }
        }

        /** Parses {@code f <v1>/<vt1>/<vn1> <v2>/<vt2>/<vn2> <v3>/<vt3>/<vn3> ...} statements, defining a face.*/
        public void parseFace(String content, Params params) {
            if (currentMaterial == null) {
                throw new RuntimeException("Missing material for face");
            }

            String[] vertices = split(content);

            for (int i = 0; i < vertices.length; i++) {
                String[] parts = vertices[i].split("/");

                if (parts.length != 3) {
                    throw new IllegalArgumentException("Expected v/vt/vn format for vertex, but got " + vertices[i]);
                }

                currentVertices.add(new Vertex(
                        positions.get(Integer.parseInt(parts[0]) - 1),
                        textures.get(Integer.parseInt(parts[1]) - 1),
                        normals.get(Integer.parseInt(parts[2]) - 1)));
            }
        }

        private void flushCurrentMesh() {
            if (currentVertices.isEmpty()) {
                return;
            }

            Identifier diffuseLocation =
                    switch (currentMaterial) {
                        case WavefrontMaterial.DiffuseTexture(Identifier location) -> location;
                    };

            meshes.put(currentMeshName, new DiffuseMesh(diffuseLocation, currentVertices.toArray(new Vertex[0])));
            currentVertices.clear();
        }

        private static String[] split(String content) {
            return content.split("\\s+");
        }
    }
}
