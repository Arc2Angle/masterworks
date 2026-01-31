package com.masterworks.masterworks.data.role;

import com.masterworks.masterworks.MasterworksRoleTypes;
import com.masterworks.masterworks.client.draw.ConstructDrawer;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.base.RenderProperty;
import com.masterworks.masterworks.location.PaletteReferenceLocation;
import com.masterworks.masterworks.location.ShapeReferenceLocation;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public record MaterialRole(List<ShapeReferenceLocation> examples) implements Role {
    public static final MapCodec<MaterialRole> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.list(ShapeReferenceLocation.CODEC).fieldOf("examples").forGetter(MaterialRole::examples))
            .apply(instance, MaterialRole::new));

    @Override
    public Type<?> type() {
        return MasterworksRoleTypes.MATERIAL.get();
    }

    @Override
    public Stream<NativeImage> render(
            Function<Construct, RenderProperty> forward, Construct.Component component, Optional<Dynamic<?>> argument) {
        PaletteReferenceLocation palette = component
                .value()
                .mapRight(construct -> new IllegalStateException())
                .orThrow()
                .registered()
                .value()
                .palette();

        ShapeReferenceLocation shape = ShapeReferenceLocation.CODEC
                .parse(argument.orElseThrow(
                        () -> new IllegalStateException("Material rendering requires a shape reference argument")))
                .getOrThrow(message -> new IllegalStateException("Failed to parse argument: " + message));

        return Stream.of(ConstructDrawer.instance().get(palette, shape));
    }
}
