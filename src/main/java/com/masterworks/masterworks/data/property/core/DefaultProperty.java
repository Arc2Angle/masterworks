package com.masterworks.masterworks.data.property.core;

import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.Material;
import com.masterworks.masterworks.data.property.TransientProperty;
import com.masterworks.masterworks.data.property.base.DataComponentProperty;
import com.masterworks.masterworks.data.property.base.LoreComponentProperty;
import com.masterworks.masterworks.init.MasterworksPropertyTypes;
import com.masterworks.masterworks.resource.location.MaterialReferenceResourceLocation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final record DefaultProperty()
        implements TransientProperty, DataComponentProperty, LoreComponentProperty {

    @Override
    public TypedDataComponent<?> getDataComponent(Construct construct) {
        return new TypedDataComponent<>(DataComponents.MAX_STACK_SIZE, 1);
    }

    @Override
    public Component getLoreComponent(Construct construct) {
        return format(construct);
    }

    @Override
    public Type type() {
        return MasterworksPropertyTypes.DEFAULT.get();
    }

    public static final class Type implements TransientProperty.Type<DefaultProperty>,
            DataComponentProperty.Type<DefaultProperty>,
            LoreComponentProperty.Type<DefaultProperty> {
        @Override
        public String name() {
            return "Default";
        }

        @Override
        public DefaultProperty create() {
            return new DefaultProperty();
        }
    }


    private static MutableComponent format(Construct construct) {
        var components = construct.components();

        if (components.size() == 1) {
            return components.values().iterator().next().value()
                    .map(DefaultProperty::formatMaterial, DefaultProperty::format);
        }

        return formatWrapBraces(components.entrySet().stream().map(entry -> {
            return entry.getValue().value()
                    .map(DefaultProperty::formatMaterial, DefaultProperty::format)
                    .append(formatComponentKey(entry.getKey()));
        }).reduce(DefaultProperty::formatJoinPlus).orElse(Component.empty()));
    }

    private static MutableComponent formatMaterial(
            MaterialReferenceResourceLocation resourceLocation) {
        Material material = resourceLocation.registered().value();
        return Component.literal(material.name()).withColor(material.color().argb());
    }

    private static MutableComponent formatComponentKey(Construct.Component.Key key) {
        return Component.literal(
                " " + Character.toUpperCase(key.value().charAt(0)) + key.value().substring(1));
    }

    private static MutableComponent formatWrapBraces(Component value) {
        return Component.literal("(").withColor(0xFFFFFF).append(value)
                .append(Component.literal(")").withColor(0xFFFFFF));
    }

    private static MutableComponent formatJoinPlus(MutableComponent left, MutableComponent right) {
        return left.append(Component.literal(" + ").withColor(0xFFFFFF)).append(right);
    }
}
