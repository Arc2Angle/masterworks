package com.masterworks.masterworks.data.role;

import com.masterworks.masterworks.MasterworksRoleTypes;
import com.masterworks.masterworks.data.Composition;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.util.codec.FlatMapCodec;
import com.masterworks.masterworks.util.vox.Voxels;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public record CompositorRole(Map<Construct.Component.Key, Role.Key> arguments) implements Role {
    public static final MapCodec<CompositorRole> CODEC = FlatMapCodec.forDispatchCodec(
                    Construct.Component.Key.CODEC, Role.Key.CODEC, new Construct.Component.Key("type"))
            .xmap(CompositorRole::new, CompositorRole::arguments);

    @Override
    public Type<?> type() {
        return MasterworksRoleTypes.COMPOSITOR.get();
    }

    @Override
    public Stream<Voxels> render(Map<Construct.Component.Key, Construct.Component> components) {
        return components.entrySet().stream().flatMap(entry -> {
            Construct.Component.Key componentKey = entry.getKey();
            Role.Key roleKey = Optional.ofNullable(arguments.get(componentKey))
                    .orElseThrow(() -> new RuntimeException("Missing key: " + componentKey));

            Construct construct = entry.getValue().constructOrThrow();
            Composition composition = construct.composition().value();
            Role role = Optional.ofNullable(composition.roles().get(roleKey))
                    .orElseThrow(
                            () -> new RuntimeException("Missing role: " + roleKey + " for component: " + componentKey));

            return role.render(construct.components());
        });
    }
}
