package com.masterworks.masterworks.data.property.base;

import java.util.function.Consumer;
import java.util.stream.Stream;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.init.tag.TypedTagKey;
import com.masterworks.masterworks.resource.location.RoleReferenceResourceLocation;

public interface ConstructProperty<T> extends Property {
    T get(Construct construct);

    @Override
    Type<?, ?> type();

    interface Type<T, P extends ConstructProperty<T>> extends Property.Type<P> {
    }

    static <T> void apply(
            TypedTagKey<Property.Type<?>, ? extends Type<T, ? extends ConstructProperty<T>>> key,
            Construct construct, Consumer<? super Stream<T>> consumer) {
        Stream<T> values = key.values().flatMap(type -> {
            try {
                ConstructProperty<T> property =
                        construct.getPropertyOrThrow(type, RoleReferenceResourceLocation.ITEM);
                T value = property.get(construct);
                return Stream.of(value);
            } catch (Construct.PropertyAccessException e) {
                return Stream.empty();
            }
        });

        consumer.accept(values);
    }
}
