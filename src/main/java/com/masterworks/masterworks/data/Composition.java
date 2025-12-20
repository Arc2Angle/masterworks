package com.masterworks.masterworks.data;

import java.util.Map;
import java.util.stream.Stream;
import com.masterworks.masterworks.data.property.PropertyContainer;
import com.masterworks.masterworks.resource.location.PropertyTypeReferenceResourceLocation;
import com.masterworks.masterworks.resource.location.RoleReferenceResourceLocation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;

public record Composition(Map<Construct.Component.Key, RoleReferenceResourceLocation> components,
        Map<RoleReferenceResourceLocation, PropertyContainer> properties) {
    public static final Codec<Composition> CODEC = new MapCodec<Composition>() {
        final static String COMPONENTS_KEY = "components";
        final static String PROPERTIES_KEY = "properties";

        static Codec<Map<Construct.Component.Key, RoleReferenceResourceLocation>> COMPONENTS_CODEC =
                Codec.unboundedMap(Construct.Component.Key.CODEC,
                        RoleReferenceResourceLocation.CODEC);

        static MapCodec<Map<Construct.Component.Key, RoleReferenceResourceLocation>> componentsMapCodec() {
            return COMPONENTS_CODEC.fieldOf(COMPONENTS_KEY);
        }

        static Codec<Map<RoleReferenceResourceLocation, PropertyContainer>> rolesCodec(
                Map<Construct.Component.Key, RoleReferenceResourceLocation> components) {
            return Codec.unboundedMap(RoleReferenceResourceLocation.CODEC,
                    PropertyTypeReferenceResourceLocation.typedMapCodec(components));
        }

        static MapCodec<Map<RoleReferenceResourceLocation, PropertyContainer>> rolesMapCodec(
                Map<Construct.Component.Key, RoleReferenceResourceLocation> components) {
            return rolesCodec(components).fieldOf(PROPERTIES_KEY);
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.of(ops.createString(COMPONENTS_KEY), ops.createString(PROPERTIES_KEY));
        }

        @Override
        public <T> DataResult<Composition> decode(DynamicOps<T> ops, MapLike<T> input) {
            return componentsMapCodec().decode(ops, input)
                    .flatMap(components -> rolesMapCodec(components).decode(ops, input)
                            .map(roles -> new Composition(components, roles)));
        }

        @Override
        public <T> RecordBuilder<T> encode(Composition input, DynamicOps<T> ops,
                RecordBuilder<T> prefix) {
            componentsMapCodec().encode(input.components(), ops, prefix);
            rolesMapCodec(input.components()).encode(input.properties(), ops, prefix);
            return prefix;
        }
    }.codec();
}
