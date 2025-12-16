package com.masterworks.masterworks.data.property;

import java.util.Map;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.resource.location.RoleReferenceResourceLocation;
import com.mojang.serialization.Decoder;

/**
 * A property is getter of values.
 */
public interface Property {
    Type<?> type();

    interface Type<T extends Property> {
        String name();

        Decoder<T> decoder(Map<Construct.Component.Key, RoleReferenceResourceLocation> components);
    }
}
