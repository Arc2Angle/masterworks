package com.masterworks.masterworks.data.property;

import java.util.Map;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.location.RoleReferenceLocation;
import com.mojang.serialization.Decoder;

/**
 * A property that is not deserialized and is created anew each time it is requested.
 * 
 * @implNote Containers are responsible for managing the lifecycle of transient properties.
 */
public interface TransientProperty extends Property {
    @Override
    Type<?> type();

    interface Type<P extends TransientProperty> extends Property.Type<P> {
        P create();

        @Override
        default Decoder<P> decoder(Map<Construct.Component.Key, RoleReferenceLocation> components) {
            throw new UnsupportedOperationException(
                    "Transient properties are not decoded from data");
        }
    }
}
