package com.masterworks.masterworks.client.part.wavefront;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.Identifier;

public record WavefrontMaterialLibrary(Map<String, WavefrontMaterial> materials) {

    public record Params(String namespace, Path parent) {}

    public static final WavefrontParser<WavefrontMaterialLibrary, Params, ?> PARSER = new WavefrontParser<>(
            Builder::new, Map.of("newmtl", Builder::parseNewMaterial, "map_Kd", Builder::parseDiffuseTexture));

    private static final class Builder implements WavefrontParser.Builder<WavefrontMaterialLibrary> {
        final Map<String, WavefrontMaterial> materials = new HashMap<>();

        String currentMaterialName = null;

        @Override
        public WavefrontMaterialLibrary build() {
            return new WavefrontMaterialLibrary(materials);
        }

        /** Parses {@code newmtl <name>} statements, which define a new material with the given name. */
        public void parseNewMaterial(String content, Params params) {
            currentMaterialName = content;
        }

        /** Parses {@code map_Kd <path>} statements, which specify a relative path to a diffuse texture for the current material. */
        public void parseDiffuseTexture(String content, Params params) {
            if (currentMaterialName == null) {
                throw new IllegalStateException("map_Kd statement before any newmtl");
            }

            Identifier id = Identifier.fromNamespaceAndPath(
                    params.namespace, params.parent.resolve(Path.of(content)).toString());
            materials.put(currentMaterialName, new WavefrontMaterial.DiffuseTexture(id));
        }
    }
}
