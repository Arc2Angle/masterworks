package com.masterworks.masterworks.data.property.base;

import java.util.List;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.init.MasterworksTags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public interface ItemAttributeModifierProperty extends Property {
    ItemAttributeModifiers.Entry getItemAttributeModifier(Construct construct);

    @Override
    Type<?> type();

    interface Type<P extends ItemAttributeModifierProperty> extends Property.Type<P> {
    }

    class Applier extends Property.Applier {
        @Override
        public void apply(Construct construct, ItemStack stack) {
            List<ItemAttributeModifiers.Entry> entries =
                    propertiesByTagKey(MasterworksTags.ITEM_ATTRIBUTE_PROPERTY_TYPES, construct)
                            .map(property -> property.getItemAttributeModifier(construct)).toList();

            if (entries.isEmpty()) {
                return;
            }

            stack.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers(entries));
        }
    }
}
