package com.masterworks.masterworks.data.property.core;

import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.MasterworksPropertyTypes;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.base.ExpressionProperty;
import com.masterworks.masterworks.data.property.base.ItemAttributeModifierProperty;
import com.masterworks.masterworks.util.Expression;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.entity.ai.attributes.Attributes;

public record ArmorProperty(Expression expression)
        implements ExpressionProperty, ItemAttributeModifierProperty {
    public static final ResourceLocation ATTRIBUTE_ID =
            ResourceLocation.fromNamespaceAndPath(MasterworksMod.ID, "armor");

    @Override
    public ItemAttributeModifiers.Entry getItemAttributeModifier(Construct construct) {
        return new ItemAttributeModifiers.Entry(Attributes.ARMOR,
                new AttributeModifier(ATTRIBUTE_ID, evaluate(construct), Operation.ADD_VALUE),
                EquipmentSlotGroup.ARMOR);
    }

    @Override
    public Type type() {
        return MasterworksPropertyTypes.ARMOR.get();
    }

    public static class Type implements ExpressionProperty.Type<ArmorProperty>,
            ItemAttributeModifierProperty.Type<ArmorProperty> {
        @Override
        public ArmorProperty create(Expression expression) {
            return new ArmorProperty(expression);
        }
    }
}
