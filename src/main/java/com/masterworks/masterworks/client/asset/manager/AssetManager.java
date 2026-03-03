package com.masterworks.masterworks.client.asset.manager;

import com.masterworks.masterworks.MasterworksMod;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public abstract class AssetManager<T> extends SimplePreparableReloadListener<Map<Identifier, T>> {
    Map<Identifier, T> values = Map.of();

    @Override
    protected void apply(Map<Identifier, T> prepared, ResourceManager resourceManager, ProfilerFiller profiler) {
        values = Map.copyOf(prepared);
        MasterworksMod.LOGGER.info("Applied {} assets to {}", values.size(), this.getClass());
    }

    public Set<Identifier> keySet() {
        return values.keySet();
    }

    public Optional<T> get(Identifier reference) {
        return Optional.ofNullable(values.get(reference));
    }
}
