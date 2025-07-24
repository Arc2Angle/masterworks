package com.masterworks.masterworks.client;

import com.masterworks.masterworks.Masterworks;
import com.masterworks.masterworks.client.model.MaterialTintSource;
import com.masterworks.masterworks.client.model.PartTypeSelectProperty;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterSelectItemModelPropertyEvent;

public class ClientEvents {
    public static void registerSelectItemModelProperties(
            RegisterSelectItemModelPropertyEvent event) {
        event.register(ResourceLocation.fromNamespaceAndPath(Masterworks.MOD_ID, "part_type"),
                PartTypeSelectProperty.TYPE);
    }

    public static void registerItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(ResourceLocation.fromNamespaceAndPath(Masterworks.MOD_ID, "material"),
                MaterialTintSource.MAP_CODEC);
    }
}
