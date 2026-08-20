package com.masterworks.masterworks.client.mesh;

import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.client.asset.AssetRegistry;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
        values = HashMap.newHashMap(prepared.size());

        for (var entry : prepared.entrySet()) {
            try {
                values.put(entry.getKey(), entry.getValue().bake(resourceManager));
            } catch (Exception e) {
                MasterworksMod.LOGGER.warn("Failed to bake {} due to {}", entry.getKey(), e);
            }
        }
    }

    @Override
    public Optional<Mesh> get(Identifier id) {
        return Optional.ofNullable(values.get(id));
    }
}
