package com.masterworks.masterworks.client.asset.manager;

import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.util.palette.Palette;
import com.masterworks.masterworks.util.registrar.ReloadListenersRegistrar;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.client.resources.VanillaClientListeners;

public final class PaletteManager extends AssetManager<Palette> {
    // assets/masterworks/textures/palettes/**.png
    private static final FileToIdConverter CONVERTER = new FileToIdConverter("textures/palette", ".png");

    @Override
    protected Map<Identifier, Palette> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<Identifier, Palette> prepared = new HashMap<>();

        for (var entry : CONVERTER.listMatchingResources(resourceManager).entrySet()) {
            Identifier id = CONVERTER.fileToId(entry.getKey());

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

        return prepared;
    }

    public static final class Type implements ReloadListenersRegistrar.Type<PaletteManager> {
        @Override
        public List<Identifier> dependents() {
            return List.of(VanillaClientListeners.MODELS);
        }

        @Override
        public PaletteManager create() {
            return new PaletteManager();
        }
    }
}
