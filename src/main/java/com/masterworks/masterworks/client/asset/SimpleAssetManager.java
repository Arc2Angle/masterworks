package com.masterworks.masterworks.client.asset;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public abstract class SimpleAssetManager<T>
        extends SimplePreparableReloadListener<Stream<Map.Entry<Identifier, Resource>>> implements AssetRegistry<T> {
    private final FileToIdConverter lister;
    private Map<Identifier, T> values;

    public SimpleAssetManager(FileToIdConverter lister) {
        this.lister = lister;
        values = Map.of();
    }

    protected abstract T read(Resource resource);

    @Override
    protected void apply(
            Stream<Map.Entry<Identifier, Resource>> prepared,
            ResourceManager resourceManager,
            ProfilerFiller profiler) {
        values = prepared.collect(Collectors.toMap(entry -> entry.getKey(), entry -> read(entry.getValue())));
    }

    @Override
    protected Stream<Map.Entry<Identifier, Resource>> prepare(
            ResourceManager resourceManager, ProfilerFiller profiler) {
        return lister.listMatchingResources(resourceManager).entrySet().stream()
                .map(entry -> Map.entry(lister.fileToId(entry.getKey()), entry.getValue()));
    }

    @Override
    public Optional<T> get(Identifier id) {
        return Optional.ofNullable(values.get(id));
    }
}
