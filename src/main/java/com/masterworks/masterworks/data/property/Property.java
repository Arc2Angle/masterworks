package com.masterworks.masterworks.data.property;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.util.BasicPropertyContainer;
import com.masterworks.masterworks.location.RoleReferenceLocation;
import com.masterworks.masterworks.util.tags.TypedTagKey;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import net.minecraft.world.item.ItemStack;

public interface Property {
    Type<?> type();

    interface Type<T extends Property> {
        Decoder<T> decoder(Map<Construct.Component.Key, RoleReferenceLocation> components);
    }

    interface Container {
        /**
         * @param <T> The property's actual generic type
         * @param type The property's registered type object
         * @return The property of the given type if present. Transient properties are created on
         *         demand.
         */
        <T extends Property> Optional<T> get(Property.Type<T> type);

        static Codec<Container> basicCodec(
                Map<Construct.Component.Key, RoleReferenceLocation> components) {
            return BasicPropertyContainer.codec(components).flatComapMap(Function.identity(),
                    container -> container instanceof BasicPropertyContainer basic
                            ? DataResult.success(basic)
                            : DataResult.error(() -> "Expected BasicPropertyContainer"));
        }
    }

    abstract class Applier {
        /**
         * Applies this property to the given item stack, based on the data in the given construct.
         */
        public abstract void apply(Construct construct, ItemStack stack);

        /**
         * Gets all properties of the given tag key from the given construct.
         * 
         * @param <P> The property type
         * @param tagKey The tag key
         * @param construct The construct to acquire properties from
         * @return A stream of properties in that tag, skipping any properties that are not present
         *         on the construct.
         * @apiNote Use it to implement extensible property application based on tags.
         */
        protected <P extends Property> Stream<? extends P> propertiesByTagKey(
                TypedTagKey<Property.Type<?>, ? extends Property.Type<? extends P>> tagKey,
                Construct construct) {
            return tagKey.values().flatMap(
                    type -> construct.properties(RoleReferenceLocation.ITEM).get(type).stream());
        }
    }
}
