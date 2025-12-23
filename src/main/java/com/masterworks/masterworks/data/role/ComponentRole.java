package com.masterworks.masterworks.data.role;

import java.util.List;
import com.masterworks.masterworks.MasterworksRoleTypes;
import com.masterworks.masterworks.location.ShapeReferenceLocation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ComponentRole(List<ShapeReferenceLocation> examples)
        implements RenderPassthroughFlagRole {
    public static final MapCodec<ComponentRole> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance
                    .group(Codec
                            .withAlternative(Codec.list(ShapeReferenceLocation.CODEC),
                                    ShapeReferenceLocation.CODEC, List::of)
                            .fieldOf("examples").forGetter(ComponentRole::examples))
                    .apply(instance, ComponentRole::new));

    @Override
    public Type<?> type() {
        return MasterworksRoleTypes.COMPONENT.get();
    }
}
