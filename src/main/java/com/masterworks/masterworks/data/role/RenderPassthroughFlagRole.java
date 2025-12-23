package com.masterworks.masterworks.data.role;

import java.util.stream.Stream;
import com.masterworks.masterworks.MasterworksPropertyTypes;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.core.RenderProperty;
import com.masterworks.masterworks.location.RoleReferenceLocation;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;

public interface RenderPassthroughFlagRole extends Role {
    @Override
    default Stream<NativeImage> render(RoleReferenceLocation self, Construct.Component component,
            Dynamic<?> argument) {
        boolean flag = Codec.BOOL.parse(argument).getOrThrow(
                message -> new IllegalStateException("Failed to parse flag: " + message));

        if (!flag) {
            return Stream.empty();
        }

        Construct construct = component.value().swap()
                .mapRight(material -> new IllegalStateException(
                        "RenderPassthroughFlagRole cannot render a material component: "
                                + component))
                .orThrow();

        RenderProperty property = construct.properties(self)
                .get(MasterworksPropertyTypes.RENDER.get())
                .orElseThrow(() -> new RuntimeException(
                        "RenderPassthroughFlagRole requires a RenderProperty on the construct: "
                                + construct));

        return property.render(construct.components());
    }
}
