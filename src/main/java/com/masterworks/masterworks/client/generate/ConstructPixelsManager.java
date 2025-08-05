package com.masterworks.masterworks.client.generate;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntUnaryOperator;
import javax.annotation.Nonnull;
import com.masterworks.masterworks.data.composition.PartDefinition;
import com.masterworks.masterworks.data.construct.Construct;
import com.masterworks.masterworks.resource.location.MaterialResourceLocation;
import com.masterworks.masterworks.resource.location.PaletteResourceLocation;
import com.masterworks.masterworks.resource.location.ShapeResourceLocation;
import com.masterworks.masterworks.util.streams.BiStream;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.ARGB;

public class ConstructPixelsManager implements Closeable, ResourceManagerReloadListener {

    private ResourceManager resourceManager;
    private NativeImage defaultPalette, defaultShape;
    // Optimization: Use Caffeine cache or a similar library to weak cache images
    // the default WeakHashMap cannot be used here as it does not support closing
    // of dropped values properly.
    private final Map<Construct, NativeImage> cache = new HashMap<>();

    private ConstructPixelsManager() {
        onResourceManagerReload(Minecraft.getInstance().getResourceManager());
    }

    private static ConstructPixelsManager instance;

    public static ConstructPixelsManager getInstance() {
        if (instance == null) {
            instance = new ConstructPixelsManager();
            Runtime.getRuntime().addShutdownHook(new Thread(instance::close));
        }
        return instance;
    }

    @Override
    public void onResourceManagerReload(@Nonnull ResourceManager manager) {
        resourceManager = manager;

        try {
            defaultPalette = PaletteResourceLocation.DEFAULT.getTexture(manager);
            defaultShape = ShapeResourceLocation.DEFAULT.getTexture(manager);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load default textures", e);
        }

        cache.clear();
    }

    @Override
    public void close() {
        defaultPalette.close();
        defaultShape.close();

        for (NativeImage image : cache.values()) {
            image.close();
        }
        cache.clear();
    }

    public NativeImage get(Construct construct) {
        if (cache.containsKey(construct)) {
            return cache.get(construct);
        }

        NativeImage pixels = generate(construct);
        cache.put(construct, pixels);
        return pixels;
    }

    private NativeImage generate(Construct construct) {
        return BiStream.zip(construct.parts().stream(), construct.getComposition().parts().stream())
                .map((materialOrPart, definition) -> materialOrPart.map(
                        material -> drawMaterial(material, definition),
                        part -> drawPart(part, definition)))
                .reduce(ConstructPixelsManager::cropOverlay).orElseThrow(
                        () -> new IllegalStateException("No parts for construct: " + construct));
    }

    private NativeImage drawMaterial(MaterialResourceLocation material, PartDefinition definition) {
        return draw(material.getMappedValue().palette(), definition.shape().orElseThrow(
                () -> new IllegalStateException("Missing shape for simple construct part")));
    }

    private NativeImage drawPart(Construct part, PartDefinition definition) {
        return definition.shape()
                .map(shape -> draw(part.getMaterialIfSingle()
                        .orElseThrow(() -> new IllegalStateException(
                                "Cannot override shape for composite construct part"))
                        .getMappedValue().palette(), shape))
                .orElseGet(() -> get(part));
    }

    private NativeImage draw(PaletteResourceLocation paletteResourceLocation,
            ShapeResourceLocation shapeResourceLocation) {
        try (NativeImage palette =
                paletteResourceLocation.getTexture(resourceManager, defaultPalette);
                NativeImage shape =
                        shapeResourceLocation.getTexture(resourceManager, defaultShape)) {

            return shape.mappedCopy(new GrayscaleColorer(palette));
        }
    }

    private static NativeImage cropOverlay(NativeImage bottom, NativeImage top) {
        int width = bottom.getWidth();
        int height = bottom.getHeight();

        NativeImage result = new NativeImage(width, height, false);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int topPixel = top.getPixel(x, y);
                int bottomPixel = bottom.getPixel(x, y);

                result.setPixel(x, y, ARGB.alpha(topPixel) != 0 ? topPixel : bottomPixel);
            }
        }

        return result;
    }

    private record GrayscaleColorer(NativeImage palette) implements IntUnaryOperator {
        @Override
        public int applyAsInt(int shapePixel) {
            int intensity = ARGB.red(shapePixel);
            int alphaMask = shapePixel | 0x00FFFFFF;
            int index = (0xFF - intensity) / 0x24;
            int color = palette.getPixel(index, 0);
            return color & alphaMask;
        }
    }

}
