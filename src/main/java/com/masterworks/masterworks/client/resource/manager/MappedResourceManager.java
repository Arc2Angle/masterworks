package com.masterworks.masterworks.client.resource.manager;

import com.masterworks.masterworks.client.resource.reference.ResourceReference;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public abstract class MappedResourceManager<R extends ResourceReference, T>
        extends SimplePreparableReloadListener<Map<ResourceLocation, T>> {
    Map<ResourceLocation, T> values = Map.of();

    @Override
    protected void apply(Map<ResourceLocation, T> prepared, ResourceManager resourceManager, ProfilerFiller profiler) {
        values = Map.copyOf(prepared);
    }

    public Optional<T> get(R reference) {
        return Optional.ofNullable(values.get(reference.id()));
    }
}
