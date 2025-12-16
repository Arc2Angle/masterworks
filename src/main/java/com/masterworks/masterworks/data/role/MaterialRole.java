package com.masterworks.masterworks.data.role;

import java.util.List;
import java.util.stream.Stream;
import com.masterworks.masterworks.client.draw.ConstructDrawer;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.init.MasterworksRoleTypes;
import com.masterworks.masterworks.resource.location.PaletteReferenceResourceLocation;
import com.masterworks.masterworks.resource.location.RoleReferenceResourceLocation;
import com.masterworks.masterworks.resource.location.ShapeReferenceResourceLocation;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record MaterialRole(List<ShapeReferenceResourceLocation> examples) implements Role {
    public static final MapCodec<MaterialRole> CODEC =
            RecordCodecBuilder
                    .mapCodec(instance -> instance
                            .group(Codec.list(ShapeReferenceResourceLocation.CODEC)
                                    .fieldOf("examples").forGetter(MaterialRole::examples))
                            .apply(instance, MaterialRole::new));

    @Override
    public Type<?> type() {
        return MasterworksRoleTypes.MATERIAL.get();
    }

    @Override
    public Stream<NativeImage> render(RoleReferenceResourceLocation self,
            Construct.Component component, Dynamic<?> argument) {
        PaletteReferenceResourceLocation palette =
                component.value().mapRight(construct -> new IllegalStateException()).orThrow()
                        .registered().value().palette();

        ShapeReferenceResourceLocation shape =
                ShapeReferenceResourceLocation.CODEC.parse(argument).getOrThrow(
                        message -> new IllegalStateException("Failed to parse shape: " + message));

        return Stream.of(ConstructDrawer.instance().get(palette, shape));
    }
}
