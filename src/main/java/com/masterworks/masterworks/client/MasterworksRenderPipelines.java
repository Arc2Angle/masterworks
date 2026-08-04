package com.masterworks.masterworks.client;

import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.util.Registrar;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

public class MasterworksRenderPipelines {
    private static final Registrar<RegisterRenderPipelinesEvent> REGISTRAR =
            new Registrar<>(MasterworksMod.ID, RegisterRenderPipelinesEvent.class);

    public static RenderPipeline register(RenderPipeline.Builder builder) {
        RenderPipeline pipeline = builder.build();
        REGISTRAR.addUnidentified(event -> event.registerPipeline(pipeline));
        return pipeline;
    }

    /** Like {@code core/entity}, but the diffuse sample is a shade index resolved through {@code PaletteSampler}. */
    public static final RenderPipeline PALETTE_ENTITY = register(RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
            .withLocation(Identifier.fromNamespaceAndPath(MasterworksMod.ID, "pipeline/palette_entity"))
            .withFragmentShader(Identifier.fromNamespaceAndPath(MasterworksMod.ID, "core/palette_entity"))
            .withBindGroupLayout(BindGroupLayouts.SAMPLER1)
            .withBindGroupLayout(MasterworksBindGroupLayouts.PALETTE_SAMPLER)
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            .withCull(false));

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }
}
