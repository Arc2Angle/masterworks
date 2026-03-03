package com.masterworks.masterworks;

import com.masterworks.masterworks.data.Material;
import com.masterworks.masterworks.util.registrar.DataMapTypesRegistrar;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public class MasterworksDataMapTypes {
    private static DataMapTypesRegistrar REGISTRAR = new DataMapTypesRegistrar(MasterworksMod.ID);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    private static <K, V> DataMapType<K, V> register(String name, ResourceKey<Registry<K>> registry, Codec<V> codec) {
        return REGISTRAR.registerDataMapType(
                name, key -> DataMapType.builder(key, registry, codec).build());
    }

    public static final DataMapType<Item, Holder<Material>> ITEM_MATERIAL = register(
            "item_material", Registries.ITEM, RegistryFixedCodec.create(MasterworksDataPackRegistries.MATERIAL));
}
