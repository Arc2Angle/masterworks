package com.masterworks.masterworks.data;

import java.util.List;
import com.masterworks.masterworks.Masterworks;
import com.masterworks.masterworks.data.composition.Composition;
import com.masterworks.masterworks.data.material.Material;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

public class Maps {
    public static void init() {}

    public static final DataMapType<Item, Material> MATERIALS = DataMapType
            .builder(Masterworks.resourceLocation("material"), Registries.ITEM, Material.CODEC)
            .build();

    public static final DataMapType<Item, List<Composition>> COMPOSITIONS =
            DataMapType.builder(Masterworks.resourceLocation("compositions"), Registries.ITEM,
                    Codec.list(Composition.CODEC)).build();

    @SubscribeEvent
    public void register(RegisterDataMapTypesEvent event) {
        event.register(MATERIALS);
        event.register(COMPOSITIONS);
    }
}
