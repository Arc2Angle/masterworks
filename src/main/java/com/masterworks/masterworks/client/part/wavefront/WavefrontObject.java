package com.masterworks.masterworks.client.part.wavefront;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Vector2f;
import org.joml.Vector3f;

public record WavefrontObject(Map<String, List<Face>> meshes) {

    public record Face(Vector3f[] positions, int color, Vector3f[] normals) {}

    public static final WavefrontParser<WavefrontObject, ?> PARSER = new WavefrontParser<>(
            WavefrontObject.Builder::new,
            Map.of(
                    "mtllib",
                    Builder::parseMaterialLibraries,
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
        List<Vector3f> positions = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        WavefrontMaterialLibrary materialLibrary = null;

        String currentMeshName = null;
        WavefrontMaterial currentMaterial = null;
        List<Face> currentFaces = new ArrayList<>();

        Map<String, List<Face>> meshes = new HashMap<>();

        @Override
        public WavefrontObject build() {
            meshes.put(currentMeshName, currentFaces);
            return new WavefrontObject(meshes);
        }

        /** Parses {@code mtllib <path>} statements, which specify a relative path to a material library. */
        public void parseMaterialLibraries(Context ctx, String content) {
            for (String relativePath : split(content)) {
                Path path = Path.of(relativePath);

                try (InputStream stream = ctx.open(path)) {
                    materialLibrary = WavefrontMaterialLibrary.PARSER
                            .parse(ctx.enter(path.getParent()), stream)
                            .merge(materialLibrary);

                } catch (IOException e) {
                    throw new RuntimeException("Failed to read material library", e);
                }
            }
        }

        /** Parses {@code o <name>} statements, which define a mesh. */
        public void parseMesh(Context ctx, String content) {
            if (currentMeshName != null) {
                meshes.put(currentMeshName, currentFaces);
                currentFaces = new ArrayList<>();
            }

            currentMeshName = content;
        }

        /** Parses {@code v <x> <y> <z>} statements, which define a vertex position. */
        public void parsePosition(Context ctx, String content) {
            positions.add(parseVector3(content));
        }

        /** Parses {@code vt <u> <v>} statements, which define a texture coordinate. */
        public void parseUV(Context ctx, String content) {
            textures.add(parseVector2(content));
        }

        /** Parses {@code vn <x> <y> <z>} statements, which define a vertex normal. */
        public void parseNormal(Context ctx, String content) {
            normals.add(parseVector3(content));
        }

        /** Parses {@code usemtl <name>} statements, which specify the name of a material to use for subsequent faces. */
        public void parseUseMaterial(Context ctx, String content) {
            if (materialLibrary == null) {
                throw new RuntimeException("No material library loaded");
            }

            currentMaterial = materialLibrary
                    .get(content)
                    .orElseThrow(() -> new RuntimeException("Material not found: " + content));
        }

        /** Parses {@code f <v1>/<vt1>/<vn1> <v2>/<vt2>/<vn2> <v3>/<vt3>/<vn3> ...} statements, defining a face.*/
        public void parseFace(Context ctx, String content) {
            if (currentMaterial == null) {
                throw new RuntimeException("Missing material for face");
            }

            String[] vertices = split(content);

            Vector3f[] positions = new Vector3f[vertices.length];
            Vector2f[] textures = new Vector2f[vertices.length];
            Vector3f[] normals = new Vector3f[vertices.length];

            for (int i = 0; i < vertices.length; i++) {
                String[] parts = vertices[i].split("/");

                if (parts.length != 3) {
                    throw new IllegalArgumentException("Expected v/vt/vn format for vertex, but got " + vertices[i]);
                }

                positions[i] = this.positions.get(Integer.parseInt(parts[0]) - 1);
                textures[i] = this.textures.get(Integer.parseInt(parts[1]) - 1);
                normals[i] = this.normals.get(Integer.parseInt(parts[2]) - 1);
            }

            currentFaces.add(new Face(positions, currentMaterial.sampleCenter(textures), normals));
        }

        private static String[] split(String content) {
            return content.split("\\s+");
        }

        private static Vector2f parseVector2(String content) {
            String[] values = split(content);

            if (values.length != 2) {
                throw new IllegalArgumentException("Invalid vector2: " + content);
            }

            return new Vector2f(Float.parseFloat(values[0]), Float.parseFloat(values[1]));
        }

        private static Vector3f parseVector3(String content) {
            String[] values = split(content);

            if (values.length != 3) {
                throw new IllegalArgumentException("Invalid vector3: " + content);
            }

            return new Vector3f(Float.parseFloat(values[0]), Float.parseFloat(values[1]), Float.parseFloat(values[2]));
        }
    }
}
