package com.masterworks.masterworks.data.property;

import java.util.Map;
import java.util.stream.Stream;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.location.RoleReferenceLocation;
import com.masterworks.masterworks.util.tags.TypedTagKey;
import com.mojang.serialization.Decoder;
import net.minecraft.world.item.ItemStack;

public interface Property {
    Type<?> type();

    interface Type<T extends Property> {
        Decoder<T> decoder(Map<Construct.Component.Key, RoleReferenceLocation> components);
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
