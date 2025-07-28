package com.masterworks.masterworks.data;

import com.masterworks.masterworks.Masterworks;
import com.masterworks.masterworks.properties.Compositions;
import com.masterworks.masterworks.properties.Material;
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

    public static final DataMapType<Item, Compositions> COMPOSITIONS =
            DataMapType.builder(Masterworks.resourceLocation("compositions"), Registries.ITEM,
                    Compositions.CODEC).build();

    @SubscribeEvent
    public void register(RegisterDataMapTypesEvent event) {
        event.register(MATERIALS);
        event.register(COMPOSITIONS);
    }
}
