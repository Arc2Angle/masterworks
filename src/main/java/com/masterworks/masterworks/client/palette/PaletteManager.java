package com.masterworks.masterworks.client.palette;

import com.masterworks.masterworks.client.asset.SimpleAssetManager;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.server.packs.resources.Resource;

public final class PaletteManager extends SimpleAssetManager<Palette> {
    public PaletteManager() {
        super(new FileToIdConverter("textures/palette", ".png"));
    }

    @Override
    protected Palette read(Resource resource) {
        try (InputStream stream = resource.open();
                NativeImage image = NativeImage.read(stream)) {
            if (image.getHeight() != 1) {
                throw new IllegalArgumentException("Palette image must be 1 pixel tall");
            }
            return new Palette(image.getPixels());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read palette", e);
        }
    }
}
