package com.masterworks.masterworks.init;

import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.client.render.ConstructSpecialModelRenderer;
import com.masterworks.masterworks.client.render.TemplateSpecialModelRenderer;
import com.masterworks.masterworks.init.registrar.SpecialModelRenderersRegistrar;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;

public class MasterworksSpecialModelRenderers {
    static final SpecialModelRenderersRegistrar REGISTRAR =
            new SpecialModelRenderersRegistrar(MasterworksMod.ID);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    static <T extends SpecialModelRenderer.Unbaked> ResourceLocation register(String name,
            MapCodec<T> codec) {
        return REGISTRAR.registerSpecialModelRenderer(name, codec);
    }



    public static final ResourceLocation CONSTRUCT =
            register("construct", ConstructSpecialModelRenderer.Unbaked.MAP_CODEC);

    public static final ResourceLocation TEMPLATE =
            register("template", TemplateSpecialModelRenderer.Unbaked.MAP_CODEC);
}
