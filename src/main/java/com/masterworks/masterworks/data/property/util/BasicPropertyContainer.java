package com.masterworks.masterworks.data.property.util;

import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.data.property.TransientProperty;
import com.masterworks.masterworks.location.PropertyTypeReferenceLocation;
import com.masterworks.masterworks.util.codec.KeyDispatchedMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class BasicPropertyContainer
        extends KeyDispatchedMap<PropertyTypeReferenceLocation, Property.Type<?>, Property>
        implements Property.Container {

    protected BasicPropertyContainer(
            Map<PropertyTypeReferenceLocation, Dynamic<?>> dynamics, Set<Construct.Component.Key> components) {
        super(dynamics, reference -> reference.registered().value(), type -> type.decoder(components));
    }

    @SuppressWarnings("unchecked")
    public <T extends Property> Optional<T> get(Property.Type<T> type) {
        return super.get(type)
                .map(value -> (T) value)
                .or(() -> type instanceof TransientProperty.Type<? extends T> transientType
                        ? Optional.of(transientType.create())
                        : Optional.empty());
    }

    public static Codec<BasicPropertyContainer> codec(Set<Construct.Component.Key> components) {
        return Codec.unboundedMap(PropertyTypeReferenceLocation.CODEC, Codec.PASSTHROUGH)
                .xmap(dynamics -> new BasicPropertyContainer(dynamics, components), instance -> instance.dynamics);
    }
}
