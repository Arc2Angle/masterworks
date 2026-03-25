package com.masterworks.masterworks;

import com.masterworks.masterworks.data.Composition;
import com.masterworks.masterworks.data.Material;
import com.masterworks.masterworks.util.Registrar;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

public class MasterworksDataPackRegistries {
    private static final Registrar<DataPackRegistryEvent.NewRegistry> REGISTRAR =
            new Registrar<>(MasterworksMod.ID, DataPackRegistryEvent.NewRegistry.class);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    private static <T> ResourceKey<Registry<T>> register(String path, Codec<T> codec) {
        return REGISTRAR
                .addIdentified(path, id -> REGISTRAR.new Wrapper<ResourceKey<Registry<T>>>() {
                    private final ResourceKey<Registry<T>> key = ResourceKey.createRegistryKey(id);

                    @Override
                    public void accept(DataPackRegistryEvent.NewRegistry event) {
                        event.dataPackRegistry(key, codec, codec);
                    }

                    @Override
                    public ResourceKey<Registry<T>> unwrap() {
                        return key;
                    }
                })
                .unwrap();
    }

    public static final ResourceKey<Registry<Material>> MATERIAL = register("material", Material.CODEC);

    public static final ResourceKey<Registry<Composition>> COMPOSITION = register("composition", Composition.CODEC);
}
