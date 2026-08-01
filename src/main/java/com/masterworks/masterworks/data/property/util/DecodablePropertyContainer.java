package com.masterworks.masterworks.data.property.util;

import com.masterworks.masterworks.MasterworksRegistries;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.data.property.TransientProperty;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class DecodablePropertyContainer implements Property.Container {
    private final Map<Property.Type<?>, ? extends Property> values;

    private DecodablePropertyContainer(Map<Property.Type<?>, ? extends Property> values) {
        this.values = values;
    }

    private static final Codec<Map<Property.Type<?>, Dynamic<?>>> TYPED_MAP_CODEC =
            Codec.unboundedMap(MasterworksRegistries.PROPERTY_TYPE.byNameCodec(), Codec.PASSTHROUGH);

    public static Decoder<Property.Container> decoder(Property.Dependencies dependencies) {
        return Decoder.ofSimple(new Decoder.Simple<Property.Container>() {
            @Override
            public <T> DataResult<Property.Container> decode(Dynamic<T> input) {
                return TYPED_MAP_CODEC
                        .parse(input)
                        .map(dynamics -> new DecodablePropertyContainer(dynamics.entrySet().stream()
                                .map(entry -> {
                                    Property.Type<?> type = entry.getKey();
                                    Dynamic<?> dynamic = entry.getValue();
                                    Property value = type.decoder(dependencies)
                                            .parse(dynamic)
                                            .getOrThrow();

                                    return Map.entry(type, value);
                                })
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))));
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Property> Optional<T> get(Property.Type<T> type) {
        return Optional.ofNullable(values.get(type))
                .map(value -> (T) value)
                .or(() -> type instanceof TransientProperty.Type<? extends T> transientType
                        ? Optional.of(transientType.create())
                        : Optional.empty());
    }
}
