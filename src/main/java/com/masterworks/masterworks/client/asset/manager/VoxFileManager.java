package com.masterworks.masterworks.client.asset.manager;

import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.util.vox.VoxFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

public final class VoxFileManager extends AssetManager<VoxFile> {
    // assets/masterworks/voxels/**.vox
    private static final FileToIdConverter CONVERTER = new FileToIdConverter("voxels", ".vox");

    @Override
    protected Map<Identifier, VoxFile> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<Identifier, VoxFile> prepared = new HashMap<>();

        for (var entry : CONVERTER.listMatchingResources(resourceManager).entrySet()) {
            Identifier id = CONVERTER.fileToId(entry.getKey());

            try (InputStream stream = entry.getValue().open()) {
                prepared.put(id, VoxFile.read(stream));
            } catch (IOException e) {
                MasterworksMod.LOGGER.error("Failed to open vox shape: {}", id, e);
            } catch (RuntimeException e) {
                MasterworksMod.LOGGER.error("Failed to parse vox shape: {}", id, e);
            }

            MasterworksMod.LOGGER.info("Loaded vox shape: {}", id);
        }

        apply(prepared, null, null); // TODO: burn this abomination
        return prepared;
    }
}
