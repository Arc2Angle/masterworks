package com.masterworks.masterworks.client.asset.manager;

import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.client.MasterworksReloadListeners;
import com.masterworks.masterworks.client.vox.VoxAsset;
import com.masterworks.masterworks.client.vox.VoxChunk;
import com.masterworks.masterworks.client.vox.VoxParser;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.client.resources.VanillaClientListeners;

public final class VoxModelManager extends AssetManager<VoxAsset> {
    // assets/masterworks/voxels/**.vox
    private static final FileToIdConverter CONVERTER = new FileToIdConverter("voxels", ".vox");

    private static final List<VoxParser<?>> STANDARD_PARSERS = List.of(
            new VoxParser.Header(),
            new VoxParser.Main(),
            new VoxParser.Pack(),
            new VoxParser.Size(),
            new VoxParser.Xyzi(),
            new VoxParser.Rgba(),
            new VoxParser.Ntrn(),
            new VoxParser.Ngrp(),
            new VoxParser.Nshp());

    private static final Function<String, VoxParser<?>> DEFAULT_PARSER = VoxParser.Unsupported::new;

    @Override
    protected Map<Identifier, VoxAsset> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<Identifier, VoxAsset> prepared = new HashMap<>();

        for (var entry : CONVERTER.listMatchingResources(resourceManager).entrySet()) {
            Identifier id = CONVERTER.fileToId(entry.getKey());

            try (InputStream stream = entry.getValue().open()) {

                ByteBuffer buffer = ByteBuffer.wrap(stream.readAllBytes()).order(ByteOrder.LITTLE_ENDIAN);
                List<VoxChunk> chunks = VoxParser.parseAll(buffer, STANDARD_PARSERS, DEFAULT_PARSER);
                prepared.put(id, VoxAsset.compile(chunks));

            } catch (IOException e) {
                MasterworksMod.LOGGER.error("Failed to open vox shape: {}", id, e);
            } catch (VoxParser.ParseException | RuntimeException e) {
                MasterworksMod.LOGGER.error("Failed to parse vox shape: {}", id, e);
            }

            MasterworksMod.LOGGER.info("Loaded vox shape: {}", id);
        }

        return prepared;
    }

    public static final class Type implements MasterworksReloadListeners.Type<VoxModelManager> {
        @Override
        public List<Identifier> dependents() {
            return List.of(VanillaClientListeners.MODELS);
        }

        @Override
        public VoxModelManager create() {
            return new VoxModelManager();
        }
    }
}
