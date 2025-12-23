package com.masterworks.masterworks.data.property.core;

import java.util.Map;
import com.masterworks.masterworks.MasterworksPropertyTypes;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.base.DataComponentProperty;
import com.masterworks.masterworks.data.property.base.ExpressionProperty;
import com.masterworks.masterworks.data.property.base.LoreComponentProperty;
import com.masterworks.masterworks.location.RoleReferenceLocation;
import com.masterworks.masterworks.util.Expression;
import com.mojang.serialization.Decoder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.chat.Component;

public record DurabilityProperty(Expression expression,
        Map<Construct.Component.Key, RoleReferenceLocation> roles)
        implements ExpressionProperty, DataComponentProperty, LoreComponentProperty {

    @Override
    public TypedDataComponent<?> getDataComponent(Construct construct) {
        return new TypedDataComponent<>(DataComponents.MAX_DAMAGE,
                evaluate(construct.components()).intValue());
    }

    @Override
    public Component getLoreComponent(Construct construct) {
        return Component.literal("Durability: " + evaluate(construct.components()).intValue());
    }

    @Override
    public Type type() {
        return MasterworksPropertyTypes.DURABILITY.get();
    }

    public static class Type extends ExpressionProperty.Type<DurabilityProperty>
            implements DataComponentProperty.Type<DurabilityProperty>,
            LoreComponentProperty.Type<DurabilityProperty> {
        @Override
        public Decoder<DurabilityProperty> decoder(
                Map<Construct.Component.Key, RoleReferenceLocation> components) {
            return decoder(DurabilityProperty::new, components);
        }
    }

}
