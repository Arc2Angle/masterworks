package com.masterworks.masterworks.data.property.core;

import javax.annotation.Nullable;
import com.masterworks.masterworks.MasterworksMod;
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

public record ToughnessProperty(Expression expression)
        implements ExpressionProperty, ItemAttributeProperty {
    public static final ResourceLocation ATTRIBUTE_ID =
            ResourceLocation.fromNamespaceAndPath(MasterworksMod.ID, "armor_toughness");

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
        return MasterworksPropertyTypes.TOUGHNESS.get();
    }

    public static class Type implements ExpressionProperty.Type<ToughnessProperty>,
            ItemAttributeProperty.Type<ToughnessProperty> {
        @Override
        public String name() {
            return "Toughness";
        }

        @Override
        public Holder<Attribute> attribute() {
            return Attributes.ARMOR_TOUGHNESS;
        }

        @Override
        public ResourceLocation id() {
            return ATTRIBUTE_ID;
        }

        @Override
        public Operation operation() {
            return Operation.ADD_VALUE;
        }

        @Override
        public EquipmentSlotGroup slot() {
            return EquipmentSlotGroup.ARMOR;
        }

        @Override
        public ToughnessProperty create(Expression expression) {
            return new ToughnessProperty(expression);
        }
    }
}
