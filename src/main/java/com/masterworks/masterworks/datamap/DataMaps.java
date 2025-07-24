package com.masterworks.masterworks.datamap;

import com.masterworks.masterworks.Masterworks;
import com.masterworks.masterworks.properties.tool.part.material.ToolPartMaterialProperties;
import com.masterworks.masterworks.properties.tool.part.type.ToolPartTypeProperties;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

public class DataMaps {
    public static final DataMapType<Item, ToolPartMaterialProperties> ITEM_TOOL_PART_MATERIAL_PROPERTIES =
            DataMapType.builder(
                    ResourceLocation.fromNamespaceAndPath(Masterworks.MOD_ID,
                            "tool_part_material_properties"),
                    Registries.ITEM, ToolPartMaterialProperties.CODEC).build();

    public static final DataMapType<Item, ToolPartTypeProperties> ITEM_TOOL_PART_TYPE_PROPERTIES =
            DataMapType.builder(
                    ResourceLocation.fromNamespaceAndPath(Masterworks.MOD_ID,
                            "tool_part_type_properties"),
                    Registries.ITEM, ToolPartTypeProperties.CODEC).build();

    public static void register(RegisterDataMapTypesEvent event) {
        event.register(ITEM_TOOL_PART_MATERIAL_PROPERTIES);
        event.register(ITEM_TOOL_PART_TYPE_PROPERTIES);
    }
}
