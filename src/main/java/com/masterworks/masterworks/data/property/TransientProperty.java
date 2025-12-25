package com.masterworks.masterworks.data.property;

/**
 * A property that may be created on demand instead of deserialized.
 * 
 * @implNote Containers are responsible for managing the lifecycle of transient properties.
 */
public interface TransientProperty extends Property {
    @Override
    Type<?> type();

    interface Type<P extends TransientProperty> extends Property.Type<P> {
        P create();
    }
}
