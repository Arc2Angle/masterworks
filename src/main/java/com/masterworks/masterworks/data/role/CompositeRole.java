package com.masterworks.masterworks.data.role;

import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.base.RenderProperty;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public interface CompositeRole extends Role {
    @Override
    default Stream<NativeImage> render(
            Function<Construct, RenderProperty> forward, Construct.Component component, Optional<Dynamic<?>> argument) {
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

        return forward.apply(construct).render(construct.components());
    }
}
