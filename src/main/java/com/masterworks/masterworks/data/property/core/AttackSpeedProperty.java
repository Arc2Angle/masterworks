package com.masterworks.masterworks.data.property.core;

import javax.annotation.Nullable;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.base.ExpressionProperty;
import com.masterworks.masterworks.data.property.base.ItemAttributeProperty;
import com.masterworks.masterworks.init.MasterworksPropertyTypes;
import com.masterworks.masterworks.util.Expression;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;

public record AttackSpeedProperty(Expression expression)
        implements ExpressionProperty, ItemAttributeProperty {
    @Override
    @Nullable
    public Double get(Construct construct) {
        Double value = evaluate(construct);
        if (value == null) {
            return null;
        }
        return value;
    }

    @Override
    public Type type() {
        return MasterworksPropertyTypes.ATTACK_SPEED.get();
    }

    public static class Type implements ExpressionProperty.Type<AttackSpeedProperty>,
            ItemAttributeProperty.Type<AttackSpeedProperty> {
        @Override
        public String name() {
            return "Attack Speed";
        }

        @Override
        public Holder<Attribute> attribute() {
            return Attributes.ATTACK_SPEED;
        }

        @Override
        public ResourceLocation id() {
            return Item.BASE_ATTACK_SPEED_ID;
        }

        @Override
        public Operation operation() {
            return Operation.ADD_VALUE;
        }

        @Override
        public EquipmentSlotGroup slot() {
            return EquipmentSlotGroup.MAINHAND;
        }

        @Override
        public AttackSpeedProperty create(Expression expression) {
            return new AttackSpeedProperty(expression);
        }
    }
}
