package com.masterworks.masterworks.client.mesh;

import com.masterworks.masterworks.client.asset.AssetRegistry;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public final class MeshManager extends SimpleJsonResourceReloadListener<MeshDefinition> implements AssetRegistry<Mesh> {
    Map<Identifier, Mesh> values;

    public MeshManager() {
        super(MeshDefinition.CODEC, new FileToIdConverter("meshes", ".json"));
        values = Map.of();
    }

    @Override
    protected void apply(
            Map<Identifier, MeshDefinition> prepared, ResourceManager resourceManager, ProfilerFiller profiler) {
        values = prepared.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey(), entry -> entry.getValue().bake(resourceManager)));
    }

    @Override
    public Optional<Mesh> get(Identifier id) {
        return Optional.ofNullable(values.get(id));
    }
}
