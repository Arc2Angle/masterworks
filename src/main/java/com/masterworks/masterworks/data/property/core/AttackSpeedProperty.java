package com.masterworks.masterworks.data.property.core;

import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.base.ExpressionProperty;
import com.masterworks.masterworks.data.property.base.ItemAttributeModifierProperty;
import com.masterworks.masterworks.init.MasterworksPropertyTypes;
import com.masterworks.masterworks.util.Expression;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public record AttackSpeedProperty(Expression expression)
        implements ExpressionProperty, ItemAttributeModifierProperty {

    @Override
    public ItemAttributeModifiers.Entry getItemAttributeModifier(Construct construct) {
        return new ItemAttributeModifiers.Entry(Attributes.ATTACK_SPEED,
                new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, evaluate(construct),
                        Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND);
    }

    @Override
    public Type type() {
        return MasterworksPropertyTypes.ATTACK_SPEED.get();
    }

    public static class Type implements ExpressionProperty.Type<AttackSpeedProperty>,
            ItemAttributeModifierProperty.Type<AttackSpeedProperty> {
        @Override
        public String name() {
            return "Attack Speed";
        }

        @Override
        public AttackSpeedProperty create(Expression expression) {
            return new AttackSpeedProperty(expression);
        }
    }
}
