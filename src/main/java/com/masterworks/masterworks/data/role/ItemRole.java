package com.masterworks.masterworks.data.role;

import java.util.List;
import com.masterworks.masterworks.MasterworksRoleTypes;
import com.masterworks.masterworks.location.ShapeReferenceLocation;
import com.mojang.serialization.MapCodec;

public record ItemRole() implements CompositeRole {
    public static final MapCodec<ItemRole> CODEC = MapCodec.unit(ItemRole::new);

    @Override
    public Type<?> type() {
        return MasterworksRoleTypes.ITEM.get();
    }

    @Override
    public List<ShapeReferenceLocation> examples() {
        return List.of();
    }
}
