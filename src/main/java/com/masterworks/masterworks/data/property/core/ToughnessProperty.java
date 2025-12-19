package com.masterworks.masterworks.data.property.core;

import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.base.ExpressionProperty;
import com.masterworks.masterworks.data.property.base.ItemAttributeProperty;
import com.masterworks.masterworks.init.MasterworksPropertyTypes;
import com.masterworks.masterworks.util.Expression;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public record ToughnessProperty(Expression expression)
        implements ExpressionProperty, ItemAttributeProperty {
    public static final ResourceLocation ATTRIBUTE_ID =
            ResourceLocation.fromNamespaceAndPath(MasterworksMod.ID, "armor_toughness");

    public ItemAttributeModifiers.Entry get(Construct construct) {
        return new ItemAttributeModifiers.Entry(Attributes.ARMOR_TOUGHNESS,
                new AttributeModifier(ATTRIBUTE_ID, evaluate(construct), Operation.ADD_VALUE),
                EquipmentSlotGroup.ARMOR);
    }

    @Override
    public Type type() {
        return MasterworksPropertyTypes.TOUGHNESS.get();
    }

    public static class Type implements ExpressionProperty.Type<ToughnessProperty>,
            ItemAttributeProperty.Type<ToughnessProperty> {
        @Override
        public String name() {
            return "Toughness";
        }

        @Override
        public ToughnessProperty create(Expression expression) {
            return new ToughnessProperty(expression);
        }
    }
}
