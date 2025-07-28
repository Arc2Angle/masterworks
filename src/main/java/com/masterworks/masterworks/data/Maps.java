package com.masterworks.masterworks.data;

import com.masterworks.masterworks.Masterworks;
import com.masterworks.masterworks.properties.Constructs;
import com.masterworks.masterworks.properties.Material;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

public class Maps {
    public static void init() {}

    public static final DataMapType<Item, Material> MATERIALS = DataMapType
            .builder(Masterworks.resourceLocation("materials"), Registries.ITEM, Material.CODEC)
            .build();

    public static final DataMapType<Item, Constructs> CONSTRUCTS = DataMapType
            .builder(Masterworks.resourceLocation("constructs"), Registries.ITEM, Constructs.CODEC)
            .build();

    @SubscribeEvent
    public void register(RegisterDataMapTypesEvent event) {
        event.register(MATERIALS);
        event.register(CONSTRUCTS);
    }
}
