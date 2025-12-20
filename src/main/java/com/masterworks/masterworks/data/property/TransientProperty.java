package com.masterworks.masterworks.data.property;

import java.util.Map;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.resource.location.RoleReferenceResourceLocation;
import com.mojang.serialization.Decoder;

public interface TransientProperty extends Property {
    @Override
    Type<?> type();

    interface Type<P extends TransientProperty> extends Property.Type<P> {
        P create();

        @Override
        default Decoder<P> decoder(
                Map<Construct.Component.Key, RoleReferenceResourceLocation> components) {
            throw new UnsupportedOperationException(
                    "Transient properties are not decoded from data");
        }
    }
}
