package com.masterworks.masterworks.resource.location;

import com.masterworks.masterworks.init.MasterworksDataMapTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public record ItemMaterialReferenceResourceLocation(ResourceLocation value)
        implements DataMappedReferenceResourceLocation<Item, MaterialReferenceResourceLocation> {

    @Override
    public Registry<Item> registry() {
        return BuiltInRegistries.ITEM;
    }

    @Override
    public DataMapType<Item, MaterialReferenceResourceLocation> dataMapType() {
        return MasterworksDataMapTypes.ITEM_MATERIAL;
    }

}
