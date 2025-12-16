package com.masterworks.masterworks.resource.location;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public interface ResourceReferenceResourceLocation extends ReferenceResourceLocation {

    public default Resource resource(ResourceManager resourceManager)
            throws MissingResourceException {
        return resourceManager.getResource(value())
                .orElseThrow(() -> new MissingResourceException(value()));
    }

    public static class MissingResourceException extends Exception {
        public MissingResourceException(ResourceLocation resourceLocation) {
            super("Failed to load asset: " + resourceLocation);
        }
    }
}
