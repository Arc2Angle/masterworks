package com.masterworks.masterworks.resource.location;

import java.io.IOException;
import java.io.InputStream;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public interface TextureReferenceResourceLocation extends ResourceReferenceResourceLocation {
    public default NativeImage texture(ResourceManager resourceManager)
            throws IOException, ResourceReferenceResourceLocation.MissingResourceException {

        Resource resource = resource(resourceManager);

        try (InputStream stream = resource.open()) {
            return NativeImage.read(stream);
        }
    }
}
