package com.masterworks.masterworks.location;

import java.util.Map;
import java.util.Optional;
import com.masterworks.masterworks.MasterworksRegistries;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.data.property.PropertyContainer;
import com.masterworks.masterworks.data.property.TransientProperty;
import com.masterworks.masterworks.util.ReferenceDispatchedMap;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record PropertyTypeReferenceLocation(ResourceLocation value)
        implements RegisteredReferenceLocation<Property.Type<?>> {

    public static final Codec<PropertyTypeReferenceLocation> CODEC = ResourceLocation.CODEC
            .xmap(PropertyTypeReferenceLocation::new, PropertyTypeReferenceLocation::value);

    public static final StreamCodec<ByteBuf, PropertyTypeReferenceLocation> STREAM_CODEC =
            ResourceLocation.STREAM_CODEC.map(PropertyTypeReferenceLocation::new,
                    PropertyTypeReferenceLocation::value);

    public Registry<Property.Type<?>> registry() {
        return MasterworksRegistries.PROPERTY_TYPE;
    }

    public static Codec<PropertyContainer> typedMapCodec(
            Map<Construct.Component.Key, RoleReferenceLocation> components) {
        return RegisteredReferenceLocation.<Property.Type<?>, PropertyTypeReferenceLocation, Property>dispatchedMapCodec(
                CODEC, type -> type.decoder(components))
                .<PropertyContainer>xmap(ReferenceDispatchedPropertyTypeMap::new,
                        boxed -> ((ReferenceDispatchedPropertyTypeMap) boxed).wrapped);
    }

    private record ReferenceDispatchedPropertyTypeMap(
            ReferenceDispatchedMap<PropertyTypeReferenceLocation, Property.Type<?>, Property> wrapped)
            implements PropertyContainer {

        @SuppressWarnings("unchecked")
        public <T extends Property> Optional<T> get(Property.Type<T> type) {
            if (type instanceof TransientProperty.Type<? extends T> transientType) {
                return Optional.of(transientType.create());
            }

            return wrapped.get(type).map(value -> (T) value);
        }
    }
}
