package com.masterworks.masterworks.client;

import java.util.function.BiFunction;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

public class MasterworksRenderTypes {
    public static final BiFunction<Identifier, Identifier, RenderType> PALETTE_ENTITY =
            Util.memoize((diffuse, palette) -> RenderType.create(
                    "palette_entity",
                    RenderSetup.builder(MasterworksRenderPipelines.PALETTE_ENTITY)
                            .withTexture("Sampler0", diffuse)
                            .withTexture("PaletteSampler", palette)
                            .useOverlay()
                            .useLightmap()
                            .createRenderSetup()));
}
