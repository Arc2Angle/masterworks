package com.masterworks.masterworks.init;

import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.init.registrar.DataMapTypesRegistrar;
import com.masterworks.masterworks.resource.location.MaterialReferenceResourceLocation;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public class MasterworksDataMapTypes {
    static DataMapTypesRegistrar REGISTRAR = new DataMapTypesRegistrar(MasterworksMod.ID);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    static <K, V> DataMapType<K, V> register(String name, ResourceKey<Registry<K>> registry,
            Codec<V> codec) {
        return REGISTRAR.registerDataMapType(name,
                key -> DataMapType.builder(key, registry, codec).build());
    }



    public static final DataMapType<Item, MaterialReferenceResourceLocation> ITEM_MATERIAL =
            register("item_material", Registries.ITEM, MaterialReferenceResourceLocation.CODEC);
}
