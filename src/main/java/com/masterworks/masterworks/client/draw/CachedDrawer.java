package com.masterworks.masterworks.client.draw;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.server.packs.resources.ResourceManager;

public abstract class CachedDrawer<T> extends PreloadingDrawer {
    // Optimization: Use Caffeine cache or a similar library to weak cache images
    // the default WeakHashMap cannot be used here as it does not support closing
    // of dropped values properly.
    private final Map<T, NativeImage> cache = new HashMap<>();

    @Override
    public void onResourceManagerReload(@Nonnull ResourceManager manager) {
        super.onResourceManagerReload(manager);
        clearCache();
    }

    @Override
    public void close() {
        super.close();
        clearCache();
    }

    public void clearCache() {
        for (NativeImage image : cache.values()) {
            image.close();
        }

        cache.clear();
    }

    protected NativeImage get(T key) {
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        NativeImage pixels = generate(key);
        cache.put(key, pixels);
        return pixels;
    }

    protected abstract NativeImage generate(T key);
}
