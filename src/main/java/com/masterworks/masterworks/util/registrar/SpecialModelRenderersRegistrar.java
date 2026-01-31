package com.masterworks.masterworks.util.registrar;

import com.mojang.serialization.MapCodec;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;

public class SpecialModelRenderersRegistrar {
    final String namespace;
    final List<Entry<?>> entries = new LinkedList<>();

    public SpecialModelRenderersRegistrar(String namespace) {
        this.namespace = namespace;
    }

    public <T extends SpecialModelRenderer.Unbaked> ResourceLocation registerSpecialModelRenderer(
            String name, MapCodec<T> codec) {
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(namespace, name);
        entries.add(new Entry<>(resourceLocation, codec));
        return resourceLocation;
    }

    public void register(IEventBus bus) {
        bus.addListener(this::addEntries);
    }

    void addEntries(RegisterSpecialModelRendererEvent event) {
        for (var entry : entries) {
            entry.apply(event);
        }
    }

    record Entry<T extends SpecialModelRenderer.Unbaked>(ResourceLocation resourceLocation, MapCodec<T> codec) {
        void apply(RegisterSpecialModelRendererEvent event) {
            event.register(resourceLocation, codec);
        }
    }
}
