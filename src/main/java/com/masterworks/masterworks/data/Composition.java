package com.masterworks.masterworks.data;

import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.data.role.Role;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public record Composition(
        Set<Construct.Component.Key> components, Map<Role.Key, Role> roles, Property.Container properties) {

    static final String COMPONENTS_KEY = "components";
    static final String ROLES_KEY = "roles";
    static final String PROPERTIES_KEY = "properties";

    static MapCodec<Set<Construct.Component.Key>> componentsMapCodec() {
        return Codec.list(Construct.Component.Key.CODEC)
                .xmap(list -> Set.copyOf(list), set -> List.copyOf(set))
                .fieldOf(COMPONENTS_KEY);
    }

    static MapCodec<Map<Role.Key, Role>> rolesMapCodec(Set<Construct.Component.Key> components) {
        // TODO: add component set validation to role codecs
        return Codec.unboundedMap(Role.Key.CODEC, Role.CODEC).fieldOf(ROLES_KEY);
    }

    static MapCodec<Property.Container> propertiesMapCodec(Set<Construct.Component.Key> components) {
        return Property.Container.basicCodec(components).fieldOf(PROPERTIES_KEY);
    }

    public static final Codec<Composition> CODEC = new MapCodec<Composition>() {
        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.of(
                    ops.createString(COMPONENTS_KEY), ops.createString(PROPERTIES_KEY), ops.createString(ROLES_KEY));
        }

        @Override
        public <T> DataResult<Composition> decode(DynamicOps<T> ops, MapLike<T> input) {
            return componentsMapCodec().decode(ops, input).flatMap(components -> propertiesMapCodec(components)
                    .decode(ops, input)
                    .flatMap(properties -> rolesMapCodec(components)
                            .decode(ops, input)
                            .map(roles -> new Composition(components, roles, properties))));
        }

        @Override
        public <T> RecordBuilder<T> encode(Composition input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            componentsMapCodec().encode(input.components(), ops, prefix);
            propertiesMapCodec(input.components()).encode(input.properties(), ops, prefix);
            rolesMapCodec(input.components()).encode(input.roles(), ops, prefix);
            return prefix;
        }
    }.codec();
}
