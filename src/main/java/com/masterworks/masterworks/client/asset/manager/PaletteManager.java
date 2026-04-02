package com.masterworks.masterworks.client.asset.manager;

import com.masterworks.masterworks.client.palette.Palette;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.resources.FileToIdConverter;

public final class PaletteManager extends NativeImageManager<Palette> {
    public PaletteManager() {
        // assets/masterworks/textures/palettes/**.png
        super(new FileToIdConverter("textures/palette", ".png"));
    }

    @Override
    protected Palette createAsset(NativeImage image) {
        return Palette.copyFromImage(image);
    }
}
