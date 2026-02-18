package com.masterworks.masterworks.data.role;

import com.masterworks.masterworks.MasterworksRegistries;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.location.RoleReferenceLocation;
import com.masterworks.masterworks.location.ShapeReferenceLocation;
import com.masterworks.masterworks.util.vox.Voxels;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface Role {
    public static final Codec<Role> CODEC =
            MasterworksRegistries.ROLE_TYPE.byNameCodec().dispatch(Role::type, Type::codec);

    List<ShapeReferenceLocation> examples();

    Type<?> type();

    Stream<Voxels> render(
            RoleReferenceLocation reference, Construct.Component component, Optional<Dynamic<?>> argument);

    record Type<T extends Role>(MapCodec<T> codec) {}
}
