package com.masterworks.masterworks.data.property.core;

import com.masterworks.masterworks.MasterworksPropertyTypes;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.base.ExpressionProperty;
import com.masterworks.masterworks.data.property.base.ItemAttributeModifierProperty;
import com.masterworks.masterworks.location.RoleReferenceLocation;
import com.masterworks.masterworks.util.Expression;
import com.mojang.serialization.Decoder;
import java.util.Map;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public record AttackDamageProperty(Expression expression, Map<Construct.Component.Key, RoleReferenceLocation> roles)
        implements ExpressionProperty, ItemAttributeModifierProperty {

    @Override
    public ItemAttributeModifiers.Entry getItemAttributeModifier(Construct construct) {
        return new ItemAttributeModifiers.Entry(
                Attributes.ATTACK_DAMAGE,
                new AttributeModifier(
                        Item.BASE_ATTACK_DAMAGE_ID, evaluate(construct.components()), Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND);
    }

    @Override
    public Type type() {
        return MasterworksPropertyTypes.ATTACK_DAMAGE.get();
    }

    public static class Type extends ExpressionProperty.Type<AttackDamageProperty>
            implements ItemAttributeModifierProperty.Type<AttackDamageProperty> {
        @Override
        public Decoder<AttackDamageProperty> decoder(Map<Construct.Component.Key, RoleReferenceLocation> components) {
            return decoder(AttackDamageProperty::new, components);
        }
    }
}
