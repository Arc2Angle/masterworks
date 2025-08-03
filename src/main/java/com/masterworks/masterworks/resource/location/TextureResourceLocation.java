package com.masterworks.masterworks.resource.location;

import java.io.IOException;
import java.io.InputStream;
import com.masterworks.masterworks.Masterworks;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public interface TextureResourceLocation extends AssetResourceLocation {
    public default NativeImage getTexture(ResourceManager resourceManager)
            throws IOException, AssetResourceLocation.MissingAssetException {

        Resource resource = getAsset(resourceManager);

        try (InputStream stream = resource.open()) {
            return NativeImage.read(stream);
        }
    }

    public default NativeImage getTexture(ResourceManager resourceManager,
            NativeImage defaultImage) {
        try {
            return getTexture(resourceManager);
        } catch (AssetResourceLocation.MissingAssetException | IOException e) {
            Masterworks.LOGGER.warn("Substituted " + value() + " with default due to", e);
            return defaultImage;
        }
    }
}
