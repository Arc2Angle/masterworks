package com.masterworks.masterworks.resource.location;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.datamaps.DataMapType;

public interface MappedItemResourceLocation<T> extends ItemResourceLocation {

    public DataMapType<Item, T> getDataMapType();

    public default T getMappedValue() {
        T result = getItemHolder().getData(getDataMapType());
        if (result == null) {
            throw new IllegalArgumentException(
                    "Data not found for item at resource location: " + value());
        }

        return result;
    }
}
