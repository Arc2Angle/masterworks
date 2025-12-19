package com.masterworks.masterworks.data.property.base;

import javax.annotation.Nullable;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.resource.location.RoleReferenceResourceLocation;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

public interface DataComponentProperty<T> extends Property {
    @Nullable
    T get(Construct construct);

    @Override
    Type<?, ?> type();


    interface Type<T, P extends DataComponentProperty<T>> extends Property.Type<P> {
        DataComponentType<T> dataComponentType();

        default void apply(Construct construct, ItemStack stack) {
            T value = construct.getPropertyOrThrow(this, RoleReferenceResourceLocation.ITEM)
                    .get(construct);
            if (value == null) {
                return;
            }

            stack.set(dataComponentType(), value);
        }
    }
}
