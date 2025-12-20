package com.masterworks.masterworks.data.property.base;

import java.util.List;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.init.MasterworksTags;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;

public interface LoreComponentProperty extends Property {
    Component getLoreComponent(Construct construct);

    @Override
    Type<?> type();

    interface Type<P extends LoreComponentProperty> extends Property.Type<P> {
    }

    static <T> void apply(Construct construct, ItemStack stack) {
        List<Component> components = ConstructPropertyHelpers
                .taggedProperties(MasterworksTags.LORE_COMPONENT_PROPERTY_TYPES, construct)
                .map(property -> property.getLoreComponent(construct)).toList();

        if (components.isEmpty()) {
            return;
        }

        stack.set(DataComponents.LORE, new ItemLore(components));
    }
}
