package com.masterworks.masterworks.data.property.util;

import com.masterworks.masterworks.MasterworksRegistries;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.data.property.TransientProperty;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class BasicPropertyContainer implements Property.Container {
    private final Map<Property.Type<?>, Dynamic<?>> dynamics;
    private final Map<Property.Type<?>, ? extends Property> values;

    private BasicPropertyContainer(
            Map<Property.Type<?>, Dynamic<?>> dynamics, Set<Construct.Component.Key> components) {

        this.dynamics = dynamics;
        this.values = dynamics.entrySet().stream()
                .map(entry -> {
                    Property.Type<?> key = entry.getKey();
                    Dynamic<?> dynamic = entry.getValue();
                    Property value = key.decoder(components).parse(dynamic).getOrThrow();

                    return Map.entry(key, value);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Codec<BasicPropertyContainer> codec(Set<Construct.Component.Key> components) {
        return Codec.unboundedMap(MasterworksRegistries.PROPERTY_TYPE.byNameCodec(), Codec.PASSTHROUGH)
                .xmap(dynamics -> new BasicPropertyContainer(dynamics, components), instance -> instance.dynamics);
    }

    @SuppressWarnings("unchecked")
    public <T extends Property> Optional<T> get(Property.Type<T> type) {
        return Optional.ofNullable(values.get(type))
                .map(value -> (T) value)
                .or(() -> type instanceof TransientProperty.Type<? extends T> transientType
                        ? Optional.of(transientType.create())
                        : Optional.empty());
    }
}
