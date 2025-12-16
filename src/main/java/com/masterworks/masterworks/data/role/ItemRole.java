package com.masterworks.masterworks.data.role;

import java.util.List;
import com.masterworks.masterworks.init.MasterworksRoleTypes;
import com.masterworks.masterworks.resource.location.ShapeReferenceResourceLocation;
import com.mojang.serialization.MapCodec;

public record ItemRole() implements RenderPassthroughFlagRole {
    public static final MapCodec<ItemRole> CODEC = MapCodec.unit(() -> new ItemRole());

    @Override
    public Type<?> type() {
        return MasterworksRoleTypes.ITEM.get();
    }

    @Override
    public List<ShapeReferenceResourceLocation> examples() {
        return List.of();
    }
}
