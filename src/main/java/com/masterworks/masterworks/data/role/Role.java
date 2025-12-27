package com.masterworks.masterworks.data.role;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import com.masterworks.masterworks.MasterworksRegistries;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.base.RenderProperty;
import com.masterworks.masterworks.location.ShapeReferenceLocation;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.MapCodec;

public interface Role {
    public static final Codec<Role> CODEC =
            MasterworksRegistries.ROLE_TYPE.byNameCodec().dispatch(Role::type, Type::codec);

    List<ShapeReferenceLocation> examples();

    Type<?> type();

    Stream<NativeImage> render(Function<Construct, RenderProperty> forward,
            Construct.Component component, Optional<Dynamic<?>> argument);

    record Type<T extends Role>(MapCodec<T> codec) {
    }
}

