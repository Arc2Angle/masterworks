package com.masterworks.masterworks.data.composition;

import com.masterworks.masterworks.MasterworksCompositionTypes;
import com.masterworks.masterworks.client.renderer.builder.MaterialRendererBuilder;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.Role;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.data.property.util.DecodablePropertyContainer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapLike;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public record ConcreteComposition(Map<Role, MaterialRendererBuilder> roles, Property.Container properties)
        implements Composition {

    @Override
    public Type<?> type() {
        return MasterworksCompositionTypes.CONCRETE.get();
    }

    public static final List<Construct.Component.Key> COMPONENTS = List.of(MaterialRendererBuilder.COMPONENT_KEY);

    @Override
    public List<Construct.Component.Key> components() {
        return COMPONENTS;
    }

    public static final MapDecoder<ConcreteComposition> DECODER = new MapDecoder.Implementation<ConcreteComposition>() {
        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.of(ops.createString(ROLES_KEY), ops.createString(PROPERTIES_KEY));
        }

        @Override
        public <T> DataResult<ConcreteComposition> decode(DynamicOps<T> ops, MapLike<T> input) {
            return ROLES_DECODER.decode(ops, input).flatMap(roles -> PROPERTIES_DECODER
                    .decode(ops, input)
                    .map(properties -> new ConcreteComposition(roles, properties)));
        }
    };

    private static final MapDecoder<Map<Role, MaterialRendererBuilder>> ROLES_DECODER =
            Codec.unboundedMap(Role.CODEC, MaterialRendererBuilder.CODEC).fieldOf(ROLES_KEY);

    private static final MapDecoder<Property.Container> PROPERTIES_DECODER = DecodablePropertyContainer.decoder(
                    new Property.Dependencies(Set.of(MaterialRendererBuilder.COMPONENT_KEY)))
            .fieldOf(PROPERTIES_KEY);
}
