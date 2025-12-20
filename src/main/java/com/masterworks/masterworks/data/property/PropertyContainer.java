package com.masterworks.masterworks.data.property;

import java.util.Optional;

public interface PropertyContainer {
    /**
     * @param <T> The property's actual generic type
     * @param type The property's registered type object
     * @return The property of the given type if present. Transient properties are created on
     *         demand.
     */
    <T extends Property> Optional<T> get(Property.Type<T> type);
}
