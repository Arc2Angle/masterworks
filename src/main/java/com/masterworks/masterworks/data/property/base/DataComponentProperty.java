package com.masterworks.masterworks.data.property.base;

import com.masterworks.masterworks.MasterworksTags;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.Property;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.world.item.ItemStack;

public interface DataComponentProperty extends Property {
    TypedDataComponent<?> getDataComponent(Construct construct);

    @Override
    Type<?> type();

    interface Type<P extends DataComponentProperty> extends Property.Type<P> {}

    class Applier extends Property.Applier {
        @Override
        public void apply(Construct construct, ItemStack stack) {
            propertiesByTagKey(MasterworksTags.DATA_COMPONENT_PROPERTY_TYPES, construct)
                    .forEach(property -> stack.set(property.getDataComponent(construct)));
        }
    }
}
