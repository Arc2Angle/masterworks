package com.masterworks.masterworks.location;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public interface TextureReferenceLocation extends ResourceReferenceLocation {
    public default NativeImage texture(ResourceManager resourceManager)
            throws IOException, ResourceReferenceLocation.MissingResourceException {

        Resource resource = resource(resourceManager);

        try (InputStream stream = resource.open()) {
            return NativeImage.read(stream);
        }
    }
}
