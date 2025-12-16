package com.masterworks.masterworks.data.property.core;

import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.data.property.provider.DataComponentProviderProperty;
import javax.annotation.Nullable;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.ExpressionProperty;
import com.masterworks.masterworks.init.MasterworksPropertyTypes;
import com.masterworks.masterworks.util.Expression;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

public record DurabilityProperty(Expression expression)
        implements ExpressionProperty, DataComponentProviderProperty<Integer> {

    @Override
    @Nullable
    public Integer get(Construct construct) {
        Double value = evaluate(construct);
        if (value == null) {
            return null;
        }

        return value.intValue();
    }

    @Override
    public Property.Type<DurabilityProperty> type() {
        return MasterworksPropertyTypes.DURABILITY.get();
    }

    public static class Type implements ExpressionProperty.Type<DurabilityProperty>,
            DataComponentProviderProperty.Type<Integer, DurabilityProperty> {
        @Override
        public String name() {
            return "Durability";
        }

        @Override
        public DataComponentType<Integer> dataComponentType() {
            return DataComponents.MAX_DAMAGE;
        }

        @Override
        public DurabilityProperty create(Expression expression) {
            return new DurabilityProperty(expression);
        }

        @Override
        public void apply(Construct construct, ItemStack stack) {
            DataComponentProviderProperty.Type.super.apply(construct, stack);
            stack.set(DataComponents.DAMAGE, 0);
        }
    }
}
