package com.masterworks.masterworks.client.util;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public abstract class ResourcelessReloadListener extends SimplePreparableReloadListener<Void> {
    @Override
    protected void apply(Void prepared, ResourceManager resourceManager, ProfilerFiller profiler) {
        onReload();
    }

    @Override
    protected Void prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        return null;
    }

    /**
     * Called when a resourcepack reload is performed. Specifically, in the {@link SimplePreparableReloadListener#apply} phase.
     */
    protected abstract void onReload();
}
