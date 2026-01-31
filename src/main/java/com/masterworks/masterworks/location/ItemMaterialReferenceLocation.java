package com.masterworks.masterworks.location;

import com.masterworks.masterworks.MasterworksDataMapTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public record ItemMaterialReferenceLocation(ResourceLocation value)
        implements DataMappedReferenceLocation<Item, MaterialReferenceLocation> {

    @Override
    public Registry<Item> registry() {
        return BuiltInRegistries.ITEM;
    }

    @Override
    public DataMapType<Item, MaterialReferenceLocation> dataMapType() {
        return MasterworksDataMapTypes.ITEM_MATERIAL;
    }
}
