package com.masterworks.masterworks.resource.location;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public interface ItemResourceLocation extends TypedResourceLocation {
    public default Holder.Reference<Item> getItemHolder() {
        return BuiltInRegistries.ITEM.get(value()).orElseThrow(() -> new IllegalArgumentException(
                "Item not found at resource location: " + value()));
    }
}
