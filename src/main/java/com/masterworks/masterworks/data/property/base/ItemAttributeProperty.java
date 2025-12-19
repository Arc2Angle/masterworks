package com.masterworks.masterworks.data.property.base;

import java.util.List;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.init.MasterworksTags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public interface ItemAttributeProperty extends ConstructProperty<ItemAttributeModifiers.Entry> {
    @Override
    Type<?> type();

    interface Type<P extends ItemAttributeProperty>
            extends ConstructProperty.Type<ItemAttributeModifiers.Entry, P> {
    }

    static void apply(Construct construct, ItemStack stack) {
        ConstructProperty.apply(MasterworksTags.ITEM_ATTRIBUTE_PROPERTY_TYPES, construct,
                values -> {
                    List<ItemAttributeModifiers.Entry> entries = values.toList();
                    if (entries.isEmpty()) {
                        return;
                    }

                    stack.set(DataComponents.ATTRIBUTE_MODIFIERS,
                            new ItemAttributeModifiers(entries));
                });
    }
}
