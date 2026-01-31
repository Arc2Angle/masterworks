package com.masterworks.masterworks.data.role;

import com.masterworks.masterworks.MasterworksRoleTypes;
import com.masterworks.masterworks.location.ShapeReferenceLocation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

public record ComponentRole(List<ShapeReferenceLocation> examples) implements CompositeRole {
    public static final MapCodec<ComponentRole> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.withAlternative(
                                    Codec.list(ShapeReferenceLocation.CODEC), ShapeReferenceLocation.CODEC, List::of)
                            .fieldOf("examples")
                            .forGetter(ComponentRole::examples))
                    .apply(instance, ComponentRole::new));

    @Override
    public Type<?> type() {
        return MasterworksRoleTypes.COMPONENT.get();
    }
}
