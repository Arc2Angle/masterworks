package com.masterworks.masterworks.data.property.core;

import com.masterworks.masterworks.MasterworksPropertyTypes;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.base.ExpressionProperty;
import com.masterworks.masterworks.data.property.base.ItemAttributeModifierProperty;
import com.masterworks.masterworks.util.Expression;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public record AttackDamageProperty(Expression expression)
        implements ExpressionProperty, ItemAttributeModifierProperty {

    @Override
    public ItemAttributeModifiers.Entry getItemAttributeModifier(Construct construct) {
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
            ItemAttributeModifierProperty.Type<AttackDamageProperty> {
        @Override
        public AttackDamageProperty create(Expression expression) {
            return new AttackDamageProperty(expression);
        }
    }
}
