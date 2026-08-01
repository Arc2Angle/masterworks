package com.masterworks.masterworks.data.property.core;

import com.masterworks.masterworks.MasterworksPropertyTypes;
import com.masterworks.masterworks.data.property.Property;
import com.mojang.serialization.Decoder;
import net.minecraft.world.entity.EquipmentSlot;

public record EquipmentSlotProperty(EquipmentSlot slot) implements Property {
    @Override
    public Type type() {
        return MasterworksPropertyTypes.EQUIPMENT_SLOT.get();
    }

    public static class Type implements Property.Type<EquipmentSlotProperty> {
        @Override
        public Decoder<EquipmentSlotProperty> decoder(Dependencies dependencies) {
            return EquipmentSlot.CODEC.xmap(EquipmentSlotProperty::new, EquipmentSlotProperty::slot);
        }
    }
}
