package com.masterworks.masterworks.client.resource.manager;

import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.util.vox.VoxFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public final class VoxFileManager extends SimplePreparableReloadListener<Map<ResourceLocation, VoxFile>> {

    // assets/masterworks/voxels/**.vox
    private static final FileToIdConverter CONVERTER = new FileToIdConverter("voxels", ".vox");

    @Override
    protected Map<ResourceLocation, VoxFile> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, VoxFile> prepared = new HashMap<>();

        for (var entry : CONVERTER.listMatchingResources(resourceManager).entrySet()) {
            ResourceLocation id = CONVERTER.fileToId(entry.getKey());

            try (InputStream stream = entry.getValue().open()) {
                prepared.put(id, VoxFile.read(stream));
            } catch (IOException e) {
                MasterworksMod.LOGGER.error("Failed to open vox shape: {}", id, e);
            } catch (RuntimeException e) {
                MasterworksMod.LOGGER.error("Failed to parse vox shape: {}", id, e);
            }

            MasterworksMod.LOGGER.info("Loaded vox shape: {}", id);
        }

        return prepared;
    }

    private Map<ResourceLocation, VoxFile> values = Map.of();

    @Override
    protected void apply(
            Map<ResourceLocation, VoxFile> prepared, ResourceManager resourceManager, ProfilerFiller profiler) {
        this.values = Map.copyOf(prepared);
    }

    public Optional<VoxFile> get(ResourceLocation id) {
        return Optional.ofNullable(values.get(id));
    }
}
