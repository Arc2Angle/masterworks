package com.masterworks.masterworks.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nonnull;
import com.masterworks.masterworks.Masterworks;
import com.masterworks.masterworks.item.ToolPartItem;
import com.masterworks.masterworks.properties.tool.part.material.ToolPartMaterialProperties;
import com.masterworks.masterworks.properties.tool.part.type.ToolPartTypeProperties;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class ToolPartTextureManager implements ResourceManagerReloadListener {

    private record DynamicResourceLocation(DynamicTexture texture,
            ResourceLocation resourceLocation) {
    }

    private final Map<ToolPartItem.Construction, DynamicResourceLocation> cache =
            new WeakHashMap<>();
    private ResourceManager resourceManager;
    private TextureManager textureManager;
    private Resource defaultPalette, defaultShape;


    private ToolPartTextureManager() {
        resourceManager = Minecraft.getInstance().getResourceManager();
        textureManager = Minecraft.getInstance().getTextureManager();

        defaultPalette = resourceManager
                .getResource(ToolPartMaterialProperties.DEFAULT.getQualifiedPalette())
                .orElseThrow();
        defaultShape = resourceManager
                .getResource(ToolPartTypeProperties.DEFAULT.getQualifiedShape()).orElseThrow();
    }


    private static ToolPartTextureManager instance;

    public static ToolPartTextureManager getInstance() {
        if (instance == null) {
            instance = new ToolPartTextureManager();
        }
        return instance;
    }


    @Override
    public void onResourceManagerReload(@Nonnull ResourceManager manager) {
        resourceManager = manager;
        cache.clear();
    }


    public ResourceLocation get(ToolPartItem.Construction construction) {
        return cache.computeIfAbsent(construction, this::generate).resourceLocation();
    }

    private DynamicResourceLocation generate(ToolPartItem.Construction construction) {
        ToolPartItem.Properties properties = construction.getProperties();
        ToolPartMaterialProperties material = properties.materialOrDefault();
        ToolPartTypeProperties type = properties.typeOrDefault();

        String textureName = String.valueOf(construction.hashCode());
        ResourceLocation location =
                Masterworks.resourceLocation("textures/item/part/" + textureName);

        try {
            NativeImage palette = getComponentImage(material.getQualifiedPalette(), defaultPalette);
            NativeImage shape = getComponentImage(type.getQualifiedShape(), defaultShape);
            NativeImage pixels = draw(shape, palette);

            try {
                DynamicTexture texture = new DynamicTexture(() -> textureName, pixels);
                textureManager.register(location, texture);
                return new DynamicResourceLocation(texture, location);
            } finally {
                palette.close();
                shape.close();
                pixels.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load textures for " + construction, e);
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

    private NativeImage draw(NativeImage shape, NativeImage palette) {
        int width = shape.getWidth();
        int height = shape.getHeight();

        NativeImage result = new NativeImage(width, height, false);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int grayscale = shape.getPixel(x, y);
                int index = (0xFF - grayscale & 0xFF) / 0x24;
                int colored = palette.getPixel(index, 0) & (grayscale | 0xFFFFFF);
                result.setPixel(x, y, colored);
            }
        }

        return result;
    }

}
