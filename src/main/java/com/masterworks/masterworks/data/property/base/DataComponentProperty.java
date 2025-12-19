package com.masterworks.masterworks.data.property.base;

import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.init.MasterworksTags;
import com.masterworks.masterworks.resource.location.RoleReferenceResourceLocation;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

public interface DataComponentProperty<T> extends ConstructProperty<T> {
    @Override
    Type<?, ?> type();

    interface Type<T, P extends DataComponentProperty<T>> extends ConstructProperty.Type<T, P> {
        DataComponentType<T> dataComponentType();

        default void apply(Construct construct, ItemStack stack) {
            try {
                P property = construct.getPropertyOrThrow(this, RoleReferenceResourceLocation.ITEM);
                stack.set(dataComponentType(), property.get(construct));
            } catch (Construct.PropertyAccessException e) {
            }
        }
    }

    static void apply(Construct construct, ItemStack stack) {
        MasterworksTags.DATA_COMPONENT_PROPERTY_TYPES.values()
                .forEach(type -> type.apply(construct, stack));
    }
}
