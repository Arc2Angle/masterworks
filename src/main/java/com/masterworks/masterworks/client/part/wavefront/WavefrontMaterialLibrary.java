package com.masterworks.masterworks.client.part.wavefront;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;

public record WavefrontMaterialLibrary(Map<String, WavefrontMaterial> materials) {

    public Optional<WavefrontMaterial> get(String name) {
        return Optional.ofNullable(materials.get(name));
    }

    /**
     * Merges the materials from another library into this one. If a material with the same name exists in both libraries, the one from {@code other} will overwrite the one in this library.
     * @param other the other material library to merge
     * @return this material library after merging
     */
    public WavefrontMaterialLibrary merge(@Nullable WavefrontMaterialLibrary other) {
        if (other != null) {
            materials.putAll(other.materials());
        }
        return this;
    }

    public static final WavefrontParser<WavefrontMaterialLibrary, ?> PARSER = new WavefrontParser<>(
            WavefrontMaterialLibrary.Builder::new,
            Map.of("newmtl", Builder::parseNewMaterial, "map_Kd", Builder::parseDiffuseTexture));

    private static final class Builder implements WavefrontParser.Builder<WavefrontMaterialLibrary> {
        Map<String, WavefrontMaterial> materials = new HashMap<>();

        String currentMaterialName = null;

        @Override
        public WavefrontMaterialLibrary build() {
            return new WavefrontMaterialLibrary(materials);
        }

        /** Parses {@code newmtl <name>} statements, which define a new material with the given name. */
        public void parseNewMaterial(Context ctx, String content) {
            currentMaterialName = content;
        }

        /** Parses {@code map_Kd <path>} statements, which specify a relative path to a diffuse texture for the current material. */
        public void parseDiffuseTexture(Context ctx, String content) {
            if (currentMaterialName == null) {
                throw new IllegalStateException("map_Kd statement before any newmtl");
            }

            try (InputStream stream = ctx.open(Path.of(content));
                    NativeImage image = NativeImage.read(stream)) {
                int width = image.getWidth();
                int height = image.getHeight();
                int[] pixels = image.getPixels();
                materials.put(
                        currentMaterialName,
                        new WavefrontMaterial.DiffuseTexture(width, height, Arrays.copyOf(pixels, pixels.length)));
            } catch (IOException e) {
                throw new RuntimeException("Failed to read texture", e);
            }
        }
    }
}
