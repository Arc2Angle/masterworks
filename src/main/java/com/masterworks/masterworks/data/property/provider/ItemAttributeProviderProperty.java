package com.masterworks.masterworks.data.property.provider;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.resource.location.RoleReferenceResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public interface ItemAttributeProviderProperty extends Property {
    @Nullable
    Double get(Construct construct);

    @Override
    Property.Type<? extends ItemAttributeProviderProperty> type();

    interface Type<P extends ItemAttributeProviderProperty> extends Property.Type<P> {
        Holder<Attribute> attribute();

        ResourceLocation id();

        AttributeModifier.Operation operation();

        EquipmentSlotGroup slot();
    }

    class Builder {
        private final List<ItemAttributeModifiers.Entry> entries = new ArrayList<>();

        public void add(Type<?> type, Construct construct) {
            Double value = construct.getPropertyOrThrow(type, RoleReferenceResourceLocation.ITEM)
                    .get(construct);
            if (value == null) {
                return;
            }

            entries.add(new ItemAttributeModifiers.Entry(type.attribute(),
                    new AttributeModifier(type.id(), value, type.operation()), type.slot()));
        }

        public void apply(ItemStack stack) {
            if (entries.isEmpty()) {
                return;
            }

            stack.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers(entries));
        }
    }
}
