package com.masterworks.masterworks.data;

import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.location.RoleReferenceLocation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.Map;
import java.util.stream.Stream;

public record Composition(
        Map<Construct.Component.Key, RoleReferenceLocation> components,
        Map<RoleReferenceLocation, Property.Container> properties) {
    static final String COMPONENTS_KEY = "components";
    static final String PROPERTIES_KEY = "properties";

    static MapCodec<Map<Construct.Component.Key, RoleReferenceLocation>> componentsMapCodec() {
        return Codec.unboundedMap(Construct.Component.Key.CODEC, RoleReferenceLocation.CODEC)
                .fieldOf(COMPONENTS_KEY);
    }

    static MapCodec<Map<RoleReferenceLocation, Property.Container>> propertiesMapCodec(
            Map<Construct.Component.Key, RoleReferenceLocation> components) {
        return Codec.unboundedMap(RoleReferenceLocation.CODEC, Property.Container.basicCodec(components))
                .fieldOf(PROPERTIES_KEY);
    }

    public static final Codec<Composition> CODEC = new MapCodec<Composition>() {
        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.of(ops.createString(COMPONENTS_KEY), ops.createString(PROPERTIES_KEY));
        }

        @Override
        public <T> DataResult<Composition> decode(DynamicOps<T> ops, MapLike<T> input) {
            return componentsMapCodec().decode(ops, input).flatMap(components -> propertiesMapCodec(components)
                    .decode(ops, input)
                    .map(properties -> new Composition(components, properties)));
        }

        @Override
        public <T> RecordBuilder<T> encode(Composition input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            componentsMapCodec().encode(input.components(), ops, prefix);
            propertiesMapCodec(input.components()).encode(input.properties(), ops, prefix);
            return prefix;
        }
    }.codec();
}
