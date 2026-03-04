package com.masterworks.masterworks.util.registrar;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.TextureAtlasStitchedEvent;

public class AtlasStitchDependenetsRegistrar {
    private final List<Entry<?>> entries = new LinkedList<>();

    public <T> Supplier<T> registerAtlasStitchListener(Factory<T> factory) {
        Entry<T> entry = new Entry<>(factory);
        entries.add(entry);
        return entry;
    }

    public void register(IEventBus bus) {
        bus.addListener(this::addEntries);
    }

    private void addEntries(TextureAtlasStitchedEvent event) {
        for (var entry : entries) {
            entry.apply(event);
        }
    }

    public static interface Factory<T> {
        Identifier atlasId();

        T create(TextureAtlas atlas);
    }

    private static final class Entry<T> implements Supplier<T> {
        private final Factory<T> factory;
        private T value = null;

        public Entry(Factory<T> factory) {
            this.factory = factory;
        }

        public void apply(TextureAtlasStitchedEvent event) {
            if (value == null && event.getAtlas().location().equals(factory.atlasId())) {
                value = factory.create(event.getAtlas());
            }
        }

        public T get() {
            if (value == null) {
                throw new IllegalStateException("Attempted to get value before it was initialized");
            }
            return value;
        }
    }
}
