package com.masterworks.masterworks.client;

import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.client.renderer.model.ConstructSpecialModelRenderer;
import com.masterworks.masterworks.client.renderer.model.TemplateSpecialModelRenderer;
import com.masterworks.masterworks.util.registrar.SpecialModelRenderersRegistrar;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;

public class MasterworksSpecialModelRenderers {
    private static final SpecialModelRenderersRegistrar REGISTRAR =
            new SpecialModelRenderersRegistrar(MasterworksMod.ID);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    private static <T extends SpecialModelRenderer.Unbaked> Identifier register(String name, MapCodec<T> codec) {
        return REGISTRAR.registerSpecialModelRenderer(name, codec);
    }

    public static final Identifier CONSTRUCT = register("construct", ConstructSpecialModelRenderer.Unbaked.MAP_CODEC);

    public static final Identifier TEMPLATE = register("template", TemplateSpecialModelRenderer.Unbaked.MAP_CODEC);
}
