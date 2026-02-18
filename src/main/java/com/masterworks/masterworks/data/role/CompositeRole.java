package com.masterworks.masterworks.data.role;

import com.masterworks.masterworks.MasterworksPropertyTypes;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.core.RenderProperty;
import com.masterworks.masterworks.location.RoleReferenceLocation;
import com.masterworks.masterworks.util.vox.Voxels;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.stream.Stream;

public interface CompositeRole extends Role {
    @Override
    default Stream<Voxels> render(
            RoleReferenceLocation reference, Construct.Component component, Optional<Dynamic<?>> argument) {
        boolean flag = argument.map(dynamic -> Codec.BOOL.parse(dynamic))
                .orElse(DataResult.success(true))
                .getOrThrow(message -> new IllegalStateException("Failed to parse flag: " + message));

        if (!flag) {
            return Stream.empty();
        }

        Construct construct = component
                .value()
                .swap()
                .mapRight(material ->
                        new IllegalStateException("CompositeRole cannot render a material component: " + component))
                .orThrow();

        RenderProperty property = construct
                .properties(reference)
                .get(MasterworksPropertyTypes.RENDER.get())
                .orElseThrow(() -> new IllegalStateException("RenderProperty not found on construct " + construct));

        return property.render(construct.components());
    }
}
