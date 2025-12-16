package com.masterworks.masterworks.data.property.core;

import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.data.property.provider.ItemAttributeProviderProperty;
import javax.annotation.Nullable;
import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.ExpressionProperty;
import com.masterworks.masterworks.init.MasterworksPropertyTypes;
import com.masterworks.masterworks.util.Expression;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;

public record ArmorProperty(Expression expression)
        implements ExpressionProperty, ItemAttributeProviderProperty {
    public static final ResourceLocation ATTRIBUTE_ID =
            ResourceLocation.fromNamespaceAndPath(MasterworksMod.ID, "armor");

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
    public Property.Type<ArmorProperty> type() {
        return MasterworksPropertyTypes.ARMOR.get();
    }

    public static class Type implements ExpressionProperty.Type<ArmorProperty>,
            ItemAttributeProviderProperty.Type<ArmorProperty> {
        @Override
        public String name() {
            return "Armor";
        }

        @Override
        public Holder<Attribute> attribute() {
            return Attributes.ARMOR;
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
        public ArmorProperty create(Expression expression) {
            return new ArmorProperty(expression);
        }
    }
}
