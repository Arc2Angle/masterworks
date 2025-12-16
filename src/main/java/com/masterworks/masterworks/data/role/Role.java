package com.masterworks.masterworks.data.role;

import java.util.List;
import java.util.stream.Stream;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.init.MasterworksRegistries;
import com.masterworks.masterworks.resource.location.RoleReferenceResourceLocation;
import com.masterworks.masterworks.resource.location.ShapeReferenceResourceLocation;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.MapCodec;

public interface Role {
    public static final Codec<Role> CODEC =
            MasterworksRegistries.ROLE_TYPE.byNameCodec().dispatch(Role::type, Type::codec);

    List<ShapeReferenceResourceLocation> examples();

    Type<?> type();

    Stream<NativeImage> render(RoleReferenceResourceLocation self, Construct.Component component,
            Dynamic<?> argument);

    record Type<T extends Role>(MapCodec<T> codec) {
    }
}

