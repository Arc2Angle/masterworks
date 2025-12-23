package com.masterworks.masterworks.client.draw;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.location.ResourceReferenceLocation;
import com.masterworks.masterworks.location.TextureReferenceLocation;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class PreloadingDrawer implements Closeable, ResourceManagerReloadListener {
    protected ResourceManager resourceManager;

    private final Map<TextureReferenceLocation, NativeImage> textures = new HashMap<>();

    @Override
    public void onResourceManagerReload(@Nonnull ResourceManager manager) {
        resourceManager = manager;

        textures.replaceAll((reference, image) -> {
            if (image != null) {
                image.close();
            }
            try {
                return reference.texture(manager);
            } catch (Exception e) {
                throw new RuntimeException("Failed to preloaded texture " + reference, e);
            }
        });
    }

    @Override
    public void close() {
        for (NativeImage image : textures.values()) {
            image.close();
        }
    }

    protected void preloadTexture(TextureReferenceLocation reference) {
        textures.put(reference, null);
    }

    protected NativeImage getPreloadedTexture(TextureReferenceLocation reference) {
        NativeImage preloadedImage = textures.get(reference);
        if (preloadedImage == null) {
            throw new RuntimeException("Texture " + reference + " not preloaded");
        }
        return preloadedImage;
    }

    protected NativeImage loadTextureWithPreloadedDefault(TextureReferenceLocation reference,
            TextureReferenceLocation defaultReference) {
        try {
            return reference.texture(resourceManager);

        } catch (ResourceReferenceLocation.MissingResourceException | IOException e) {
            MasterworksMod.LOGGER.warn(
                    "Substituting " + reference + " with default " + defaultReference + " due to",
                    e);
            return getPreloadedTexture(defaultReference);
        }
    }
}
