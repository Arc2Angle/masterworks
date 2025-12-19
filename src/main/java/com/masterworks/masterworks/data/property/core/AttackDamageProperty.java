package com.masterworks.masterworks.data.property.core;

import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.base.ExpressionProperty;
import com.masterworks.masterworks.data.property.base.ItemAttributeProperty;
import com.masterworks.masterworks.init.MasterworksPropertyTypes;
import com.masterworks.masterworks.util.Expression;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public record AttackDamageProperty(Expression expression)
        implements ExpressionProperty, ItemAttributeProperty {

    public ItemAttributeModifiers.Entry get(Construct construct) {
        return new ItemAttributeModifiers.Entry(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, evaluate(construct),
                        Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND);
    }

    @Override
    public Type type() {
        return MasterworksPropertyTypes.ATTACK_DAMAGE.get();
    }

    public static class Type implements ExpressionProperty.Type<AttackDamageProperty>,
            ItemAttributeProperty.Type<AttackDamageProperty> {
        @Override
        public String name() {
            return "Attack Damage";
        }

        @Override
        public AttackDamageProperty create(Expression expression) {
            return new AttackDamageProperty(expression);
        }
    }
}
