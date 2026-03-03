package com.masterworks.masterworks.data.property.core;

import com.masterworks.masterworks.MasterworksPropertyTypes;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.TransientProperty;
import com.masterworks.masterworks.data.property.base.DataComponentProperty;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import java.util.Set;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;

public record MaxStackSizeProperty(int size) implements TransientProperty, DataComponentProperty {
    @Override
    public TypedDataComponent<?> getDataComponent(Construct construct) {
        return new TypedDataComponent<>(DataComponents.MAX_STACK_SIZE, size);
    }

    @Override
    public Type type() {
        return MasterworksPropertyTypes.MAX_STACK_SIZE.get();
    }

    public static final class Type
            implements TransientProperty.Type<MaxStackSizeProperty>, DataComponentProperty.Type<MaxStackSizeProperty> {
        @Override
        public MaxStackSizeProperty create() {
            return new MaxStackSizeProperty(1);
        }

        @Override
        public Decoder<MaxStackSizeProperty> decoder(Set<Construct.Component.Key> components) {
            return Codec.INT.xmap(MaxStackSizeProperty::new, MaxStackSizeProperty::size);
        }
    }
}
