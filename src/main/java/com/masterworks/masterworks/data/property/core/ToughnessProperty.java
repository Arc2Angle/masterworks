package com.masterworks.masterworks.data.property.core;

import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.MasterworksPropertyTypes;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.base.ExpressionProperty;
import com.masterworks.masterworks.data.property.base.ItemAttributeModifierProperty;
import com.masterworks.masterworks.util.Expression;
import com.mojang.serialization.Decoder;
import java.util.Set;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public record ToughnessProperty(Expression expression) implements ExpressionProperty, ItemAttributeModifierProperty {
    public static final Identifier ATTRIBUTE_ID = Identifier.fromNamespaceAndPath(MasterworksMod.ID, "armor_toughness");

    @Override
    public ItemAttributeModifiers.Entry getItemAttributeModifier(Construct construct) {
        return new ItemAttributeModifiers.Entry(
                Attributes.ARMOR_TOUGHNESS,
                new AttributeModifier(ATTRIBUTE_ID, evaluate(construct.components()), Operation.ADD_VALUE),
                EquipmentSlotGroup.ARMOR);
    }

    @Override
    public Type type() {
        return MasterworksPropertyTypes.TOUGHNESS.get();
    }

    public static class Type extends ExpressionProperty.Type<ToughnessProperty>
            implements ItemAttributeModifierProperty.Type<ToughnessProperty> {
        @Override
        public Decoder<ToughnessProperty> decoder(Set<Construct.Component.Key> components) {
            return decoder(ToughnessProperty::new, components);
        }
    }
}
