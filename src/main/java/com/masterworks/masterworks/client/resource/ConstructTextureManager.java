package com.masterworks.masterworks.client.resource;

import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nonnull;
import com.masterworks.masterworks.Masterworks;
import com.masterworks.masterworks.data.construct.Construct;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class ConstructTextureManager implements ResourceManagerReloadListener {

    private final Map<Construct, DynamicResourceLocation> cache = new WeakHashMap<>();

    private TextureManager textureManager;

    private ConstructTextureManager() {
        textureManager = Minecraft.getInstance().getTextureManager();
    }

    private static ConstructTextureManager instance;

    public static ConstructTextureManager getInstance() {
        if (instance == null) {
            instance = new ConstructTextureManager();
        }
        return instance;
    }

    @Override
    public void onResourceManagerReload(@Nonnull ResourceManager manager) {
        cache.clear();
    }

    public ResourceLocation get(Construct construct) {
        return cache.computeIfAbsent(construct, this::generate).resourceLocation();
    }

    private DynamicResourceLocation generate(Construct construct) {
        String name = String.valueOf(construct.hashCode());
        ResourceLocation location = Masterworks.resourceLocation("textures/item/construct/" + name);

        NativeImage pixels = ConstructPixelsManager.getInstance().get(construct);

        try {
            DynamicTexture texture = new DynamicTexture(() -> name, pixels);
            textureManager.register(location, texture);
            return new DynamicResourceLocation(texture, location);
        } finally {
            pixels.close();
        }
    }

    private record DynamicResourceLocation(DynamicTexture texture,
            ResourceLocation resourceLocation) {
    }
}
