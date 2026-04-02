package com.masterworks.masterworks.client.asset.manager;

import com.masterworks.masterworks.client.etching.Etching;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.resources.FileToIdConverter;

public final class EtchingManager extends NativeImageManager<Etching> {
    public EtchingManager() {
        // assets/masterworks/textures/etching/**.png
        super(new FileToIdConverter("textures/etching", ".png"));
    }

    @Override
    protected Etching createAsset(NativeImage image) {
        return Etching.copyFromImage(image);
    }
}
