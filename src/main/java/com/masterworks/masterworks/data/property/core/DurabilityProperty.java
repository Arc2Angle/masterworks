package com.masterworks.masterworks.data.property.core;

import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.base.DataComponentProperty;
import com.masterworks.masterworks.data.property.base.ExpressionProperty;
import com.masterworks.masterworks.data.property.base.LoreComponentProperty;
import com.masterworks.masterworks.init.MasterworksPropertyTypes;
import com.masterworks.masterworks.util.Expression;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.chat.Component;

public record DurabilityProperty(Expression expression)
        implements ExpressionProperty, DataComponentProperty, LoreComponentProperty {

    @Override
    public TypedDataComponent<?> getDataComponent(Construct construct) {
        return new TypedDataComponent<>(DataComponents.MAX_DAMAGE, evaluate(construct).intValue());
    }

    @Override
    public Component getLoreComponent(Construct construct) {
        return Component.literal("Durability: " + evaluate(construct).intValue());
    }

    @Override
    public Type type() {
        return MasterworksPropertyTypes.DURABILITY.get();
    }

    public static class Type implements ExpressionProperty.Type<DurabilityProperty>,
            DataComponentProperty.Type<DurabilityProperty>,
            LoreComponentProperty.Type<DurabilityProperty> {
        @Override
        public DurabilityProperty create(Expression expression) {
            return new DurabilityProperty(expression);
        }
    }

}
