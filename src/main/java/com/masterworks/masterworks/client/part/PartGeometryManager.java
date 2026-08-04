package com.masterworks.masterworks.client.part;

import com.masterworks.masterworks.client.asset.AssetRegistry;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public final class PartGeometryManager extends SimpleJsonResourceReloadListener<PartDefinition>
        implements AssetRegistry<PartGeometry> {
    Map<Identifier, PartGeometry> values;

    public PartGeometryManager() {
        super(PartDefinition.CODEC, new FileToIdConverter("parts/definition", ".json"));
        values = Map.of();
    }

    @Override
    protected void apply(
            Map<Identifier, PartDefinition> prepared, ResourceManager resourceManager, ProfilerFiller profiler) {
        values = prepared.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey(), entry -> entry.getValue().bake(resourceManager)));
    }

    @Override
    public Optional<PartGeometry> get(Identifier id) {
        return Optional.ofNullable(values.get(id));
    }
}
