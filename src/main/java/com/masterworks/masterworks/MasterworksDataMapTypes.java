package com.masterworks.masterworks;

import com.masterworks.masterworks.data.Material;
import com.masterworks.masterworks.util.Registrar;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

public class MasterworksDataMapTypes {
    private static Registrar<RegisterDataMapTypesEvent> REGISTRAR =
            new Registrar<>(MasterworksMod.ID, RegisterDataMapTypesEvent.class);

    private static <K, V> DataMapType<K, V> register(String path, ResourceKey<Registry<K>> registry, Codec<V> codec) {
        return REGISTRAR
                .addIdentified(path, id -> REGISTRAR.new Wrapper<DataMapType<K, V>>() {
                    private final DataMapType<K, V> type =
                            DataMapType.builder(id, registry, codec).build();

                    @Override
                    public void accept(RegisterDataMapTypesEvent event) {
                        event.register(type);
                    }

                    @Override
                    public DataMapType<K, V> unwrap() {
                        return type;
                    }
                })
                .unwrap();
    }

    public static final DataMapType<Item, Holder<Material>> ITEM_MATERIAL = register(
            "item_material", Registries.ITEM, RegistryFixedCodec.create(MasterworksDataPackRegistries.MATERIAL));

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }
}
