package com.masterworks.masterworks.client.resource.manager;

import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.client.resource.reference.PaletteResourceReference;
import com.masterworks.masterworks.util.palette.Palette;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

public final class PaletteManager extends MappedResourceManager<PaletteResourceReference, Palette> {
    // assets/masterworks/textures/palettes/**.png
    private static final FileToIdConverter CONVERTER = new FileToIdConverter("textures/palette", ".png");

    @Override
    protected Map<ResourceLocation, Palette> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<ResourceLocation, Palette> prepared = new HashMap<>();

        for (var entry : CONVERTER.listMatchingResources(resourceManager).entrySet()) {
            ResourceLocation id = CONVERTER.fileToId(entry.getKey());

            try (InputStream stream = entry.getValue().open()) {
                try (NativeImage image = NativeImage.read(stream)) {
                    prepared.put(id, Palette.copyFromImage(image));

                } catch (IOException e) {
                    MasterworksMod.LOGGER.error("Failed to read palette: {}", id, e);
                }

            } catch (IOException e) {
                MasterworksMod.LOGGER.error("Failed to open palette: {}", id, e);
            }

            MasterworksMod.LOGGER.info("Loaded palette: {}", id);
        }

        apply(prepared, null, null); // TODO: burn this abomination
        return prepared;
    }
}
