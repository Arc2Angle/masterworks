package com.masterworks.masterworks.client.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntUnaryOperator;
import javax.annotation.Nonnull;
import com.masterworks.masterworks.data.composition.PartDefinition;
import com.masterworks.masterworks.data.construct.Construct;
import com.masterworks.masterworks.data.material.Material;
import com.masterworks.masterworks.util.Streams;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class ConstructPixelsManager implements ResourceManagerReloadListener {

    // Optimization: Use Caffeine cache or a similar library to weak cache images
    // the default WeakHashMap cannot be used here as it does not support closing
    // of dropped values properly.
    private final Map<Construct, NativeImage> cache = new HashMap<>();

    private ResourceManager resourceManager;
    private Resource defaultPalette, defaultShape;

    private ConstructPixelsManager() {
        onResourceManagerReload(Minecraft.getInstance().getResourceManager());
    }

    private static ConstructPixelsManager instance;

    public static ConstructPixelsManager getInstance() {
        if (instance == null) {
            instance = new ConstructPixelsManager();
        }
        return instance;
    }

    @Override
    public void onResourceManagerReload(@Nonnull ResourceManager manager) {
        resourceManager = manager;

        defaultPalette =
                resourceManager.getResource(Material.DEFAULT.getQualifiedPalette()).orElseThrow();
        defaultShape = null;
        // resourceManager.getResource(ToolPartTypeProperties.DEFAULT.getQualifiedShape())
        // .orElseThrow();

        cache.clear();
    }

    public NativeImage get(Construct construct) {
        return cache.computeIfAbsent(construct, this::generate);
    }

    private NativeImage generate(Construct construct) {
        return Streams.zip(construct.parts().stream(), construct.getComposition().parts().stream())
                .map((value, definition) -> value.map(material -> draw(material, definition),
                        part -> definition.shape().isEmpty() ? get(part) : get(part)))
                .reduce(ConstructPixelsManager::cropOverlay)
                .orElseThrow(() -> new IllegalStateException(
                        "No parts found for construct: " + construct));
    }

    private NativeImage draw(ResourceLocation materialItem, PartDefinition definition) {
        try (NativeImage palette = getComponentImage(
                Material.fromItem(materialItem).getQualifiedPalette(), defaultPalette);
                NativeImage shape =
                        getComponentImage(definition.getQualifiedShapeOrThrow(), defaultShape)) {

            return shape.mappedCopy(new GrayscaleColorer(palette));

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load textures for " + materialItem + " x " + definition, e);
        }
    }

    private NativeImage getComponentImage(ResourceLocation location, Resource defaultResource)
            throws IOException {
        Resource resource = resourceManager.getResource(location).orElse(defaultResource);
        InputStream stream = resource.open();
        try {
            return NativeImage.read(stream);
        } finally {
            stream.close();
        }
    }

    private static NativeImage cropOverlay(NativeImage bottom, NativeImage top) {
        int width = bottom.getWidth();
        int height = bottom.getHeight();

        NativeImage result = new NativeImage(width, height, true);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int topPixel = top.getPixel(x, y);
                int bottomPixel = bottom.getPixel(x, y);

                int topAlpha = topPixel & 0xFF000000;
                result.setPixel(x, y, topAlpha > 0 ? topPixel : bottomPixel);
            }
        }

        return result;
    }

    private record GrayscaleColorer(NativeImage palette) implements IntUnaryOperator {
        @Override
        public int applyAsInt(int shapePixel) {
            int grayness = shapePixel & 0x000000FF;
            int alphaMask = shapePixel | 0x00FFFFFF;
            int index = (0xFF - grayness) / 0x24;
            int color = palette.getPixel(index, 0);
            return color & alphaMask;
        }
    }

}
