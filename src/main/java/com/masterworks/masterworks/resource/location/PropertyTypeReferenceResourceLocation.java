package com.masterworks.masterworks.resource.location;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.data.property.PropertyTypeMap;
import com.masterworks.masterworks.init.MasterworksRegistries;
import com.masterworks.masterworks.util.ReferenceDispatchedMap;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record PropertyTypeReferenceResourceLocation(ResourceLocation value)
        implements RegisteredReferenceResourceLocation<Property.Type<?>> {

    public static final Codec<PropertyTypeReferenceResourceLocation> CODEC =
            ResourceLocation.CODEC.xmap(PropertyTypeReferenceResourceLocation::new,
                    PropertyTypeReferenceResourceLocation::value);

    public static final StreamCodec<ByteBuf, PropertyTypeReferenceResourceLocation> STREAM_CODEC =
            ResourceLocation.STREAM_CODEC.map(PropertyTypeReferenceResourceLocation::new,
                    PropertyTypeReferenceResourceLocation::value);

    public Registry<Property.Type<?>> registry() {
        return MasterworksRegistries.PROPERTY_TYPE;
    }

    public static Stream<PropertyTypeReferenceResourceLocation> all() {
        return MasterworksRegistries.PROPERTY_TYPE.keySet().stream()
                .map(PropertyTypeReferenceResourceLocation::new);
    }

    public static Codec<PropertyTypeMap> typedMapCodec(
            Map<Construct.Component.Key, RoleReferenceResourceLocation> components) {
        return RegisteredReferenceResourceLocation.<Property.Type<?>, PropertyTypeReferenceResourceLocation, Property>dispatchedMapCodec(
                CODEC, type -> type.decoder(components))
                .<PropertyTypeMap>xmap(ReferenceDispatchedPropertyTypeMap::new,
                        boxed -> ((ReferenceDispatchedPropertyTypeMap) boxed).wrapped);
    }

    private record ReferenceDispatchedPropertyTypeMap(
            ReferenceDispatchedMap<PropertyTypeReferenceResourceLocation, Property.Type<?>, Property> wrapped)
            implements PropertyTypeMap {

        @SuppressWarnings("unchecked")
        public <T extends Property> Optional<T> get(Property.Type<T> type) {
            return wrapped.get(type).map(value -> (T) value);
        }
    }
}
