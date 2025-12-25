package com.masterworks.masterworks.data.property.util;

import java.util.Map;
import java.util.Optional;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.data.property.TransientProperty;
import com.masterworks.masterworks.location.PropertyTypeReferenceLocation;
import com.masterworks.masterworks.location.RoleReferenceLocation;
import com.masterworks.masterworks.util.codec.KeyDispatchedMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;

public final class BasicPropertyContainer
        extends KeyDispatchedMap<PropertyTypeReferenceLocation, Property.Type<?>, Property>
        implements Property.Container {

    protected BasicPropertyContainer(Map<PropertyTypeReferenceLocation, Dynamic<?>> dynamics,
            Map<Construct.Component.Key, RoleReferenceLocation> components) {
        super(dynamics, reference -> reference.registered().value(),
                type -> type.decoder(components));
    }

    @SuppressWarnings("unchecked")
    public <T extends Property> Optional<T> get(Property.Type<T> type) {
        return super.get(type).map(value -> (T) value)
                .or(() -> type instanceof TransientProperty.Type<? extends T> transientType
                        ? Optional.of(transientType.create())
                        : Optional.empty());
    }

    public static Codec<BasicPropertyContainer> codec(
            Map<Construct.Component.Key, RoleReferenceLocation> components) {
        return Codec.unboundedMap(PropertyTypeReferenceLocation.CODEC, Codec.PASSTHROUGH).xmap(
                dynamics -> new BasicPropertyContainer(dynamics, components),
                instance -> instance.dynamics);
    }
}
