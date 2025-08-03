package com.masterworks.masterworks.resource.location;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public interface AssetResourceLocation extends TypedResourceLocation {

    public default Resource getAsset(ResourceManager resourceManager) throws MissingAssetException {
        return resourceManager.getResource(value())
                .orElseThrow(() -> new MissingAssetException(value()));
    }

    public static class MissingAssetException extends Exception {
        public MissingAssetException(ResourceLocation resourceLocation) {
            super("Failed to load asset: " + resourceLocation);
        }
    }
}
