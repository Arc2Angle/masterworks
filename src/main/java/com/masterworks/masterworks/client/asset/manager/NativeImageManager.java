package com.masterworks.masterworks.client.asset.manager;

import com.masterworks.masterworks.MasterworksMod;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

public abstract class NativeImageManager<T> extends AssetManager<T> {
    private final FileToIdConverter converter;

    protected NativeImageManager(FileToIdConverter converter) {
        this.converter = converter;
    }

    protected abstract T createAsset(NativeImage image);

    @Override
    protected Map<Identifier, T> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<Identifier, T> prepared = new HashMap<>();

        for (Map.Entry<Identifier, Resource> entry :
                converter.listMatchingResources(resourceManager).entrySet()) {
            Identifier id = converter.fileToId(entry.getKey());

            try (InputStream stream = entry.getValue().open()) {
                try (NativeImage image = NativeImage.read(stream)) {
                    prepared.put(id, createAsset(image));

                } catch (IOException e) {
                    MasterworksMod.LOGGER.error("Failed to read image: {}", id, e);
                }

            } catch (IOException e) {
                MasterworksMod.LOGGER.error("Failed to open image: {}", id, e);
            }

            MasterworksMod.LOGGER.info("Loaded image: {}", id);
        }

        return prepared;
    }
}
