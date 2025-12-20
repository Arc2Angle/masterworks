package com.masterworks.masterworks.data.property.base;

import java.util.stream.Stream;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.init.tag.TypedTagKey;
import com.masterworks.masterworks.resource.location.RoleReferenceResourceLocation;

public interface ConstructPropertyHelpers {
    static <P extends Property> Stream<? extends P> taggedProperties(
            TypedTagKey<Property.Type<?>, ? extends Property.Type<? extends P>> key,
            Construct construct) {
        return key.values().flatMap(type -> {
            try {
                P property = construct.getPropertyOrThrow(type, RoleReferenceResourceLocation.ITEM);
                return Stream.of(property);
            } catch (Construct.PropertyAccessException e) {
                return Stream.empty();
            }
        });
    }
}
