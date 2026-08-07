package com.masterworks.masterworks.client.mesh.wavefront;

import net.minecraft.resources.Identifier;

public sealed interface WavefrontMaterial {
    record DiffuseTexture(Identifier location) implements WavefrontMaterial {}
}
