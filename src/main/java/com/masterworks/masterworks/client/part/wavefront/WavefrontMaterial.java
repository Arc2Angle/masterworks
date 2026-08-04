package com.masterworks.masterworks.client.part.wavefront;

import net.minecraft.resources.Identifier;

public sealed interface WavefrontMaterial {
    record DiffuseTexture(Identifier id) implements WavefrontMaterial {}
}
