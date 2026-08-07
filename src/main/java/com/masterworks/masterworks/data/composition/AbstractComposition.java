package com.masterworks.masterworks.data.composition;

import com.masterworks.masterworks.MasterworksCompositionTypes;
import com.masterworks.masterworks.client.renderlet.CompositeRenderlet;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.Role;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.data.property.util.DecodablePropertyContainer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapLike;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public record AbstractComposition(
        List<Construct.Component.Key> components, Map<Role, CompositeRenderlet> roles, Property.Container properties)
        implements Composition {

    @Override
    public Type<?> type() {
        return MasterworksCompositionTypes.ABSTRACT.get();
    }

    public static final MapDecoder<AbstractComposition> DECODER = new MapDecoder.Implementation<AbstractComposition>() {
        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.of(
                    ops.createString(COMPONENTS_KEY), ops.createString(ROLES_KEY), ops.createString(PROPERTIES_KEY));
        }

        @Override
        public <T> DataResult<AbstractComposition> decode(DynamicOps<T> ops, MapLike<T> input) {
            return COMPONENTS_DECODER.decode(ops, input).flatMap(components -> rolesDecoder(components)
                    .decode(ops, input)
                    .flatMap(roles -> propertiesDecoder(components)
                            .decode(ops, input)
                            .map(properties -> new AbstractComposition(components, roles, properties))));
        }
    };

    private static final MapDecoder<List<Construct.Component.Key>> COMPONENTS_DECODER =
            Codec.list(Construct.Component.Key.CODEC).fieldOf(COMPONENTS_KEY);

    private static MapDecoder<Map<Role, CompositeRenderlet>> rolesDecoder(List<Construct.Component.Key> components) {
        return Codec.unboundedMap(Role.CODEC, CompositeRenderlet.CODEC).fieldOf(ROLES_KEY);
    }

    private static MapDecoder<Property.Container> propertiesDecoder(List<Construct.Component.Key> components) {
        return DecodablePropertyContainer.decoder(new Property.Dependencies(new HashSet<>(components)))
                .fieldOf(PROPERTIES_KEY);
    }
}
