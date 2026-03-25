package com.masterworks.masterworks.client;

import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.client.renderer.model.ConstructSpecialModelRenderer;
import com.masterworks.masterworks.client.renderer.model.TemplateSpecialModelRenderer;
import com.masterworks.masterworks.util.Registrar;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;

public class MasterworksSpecialModelRenderers {
    private static final Registrar<RegisterSpecialModelRendererEvent> REGISTRAR =
            new Registrar<>(MasterworksMod.ID, RegisterSpecialModelRendererEvent.class);

    private static <T extends SpecialModelRenderer.Unbaked> Identifier register(String path, MapCodec<T> codec) {
        return REGISTRAR
                .addIdentified(path, id -> REGISTRAR.new Wrapper<Identifier>() {
                    @Override
                    public void accept(RegisterSpecialModelRendererEvent event) {
                        event.register(id, codec);
                    }

                    @Override
                    public Identifier unwrap() {
                        return id;
                    }
                })
                .unwrap();
    }

    public static final Identifier CONSTRUCT = register("construct", ConstructSpecialModelRenderer.Unbaked.MAP_CODEC);

    public static final Identifier TEMPLATE = register("template", TemplateSpecialModelRenderer.Unbaked.MAP_CODEC);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }
}
