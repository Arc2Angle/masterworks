package com.masterworks.masterworks.data.property.base;

import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.init.MasterworksTags;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.world.item.ItemStack;

public interface DataComponentProperty extends Property {
    TypedDataComponent<?> getDataComponent(Construct construct);

    @Override
    Type<?> type();

    interface Type<P extends DataComponentProperty> extends Property.Type<P> {
    }

    static void apply(Construct construct, ItemStack stack) {
        ConstructPropertyHelpers
                .taggedProperties(MasterworksTags.DATA_COMPONENT_PROPERTY_TYPES, construct)
                .forEach(property -> stack.set(property.getDataComponent(construct)));
    }
}
