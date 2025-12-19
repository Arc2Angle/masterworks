package com.masterworks.masterworks.data.property.core;

import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.base.DataComponentProperty;
import com.masterworks.masterworks.data.property.base.ExpressionProperty;
import com.masterworks.masterworks.init.MasterworksPropertyTypes;
import com.masterworks.masterworks.util.Expression;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;

public record DurabilityProperty(Expression expression)
        implements ExpressionProperty, DataComponentProperty<Integer> {

    @Override
    public Integer get(Construct construct) {
        return evaluate(construct).intValue();
    }

    @Override
    public Type type() {
        return MasterworksPropertyTypes.DURABILITY.get();
    }

    public static class Type implements ExpressionProperty.Type<DurabilityProperty>,
            DataComponentProperty.Type<Integer, DurabilityProperty> {
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
    }
}
