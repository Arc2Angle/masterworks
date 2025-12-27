package com.masterworks.masterworks.data.property.core;

import java.util.Map;
import com.masterworks.masterworks.MasterworksPropertyTypes;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.location.RoleReferenceLocation;
import com.mojang.serialization.Decoder;
import net.minecraft.world.entity.EquipmentSlot;

public record EquipmentSlotProperty(EquipmentSlot slot) implements Property {
    @Override
    public Type type() {
        return MasterworksPropertyTypes.EQUIPMENT_SLOT.get();
    }

    public static class Type implements Property.Type<EquipmentSlotProperty> {
        @Override
        public Decoder<EquipmentSlotProperty> decoder(
                Map<Construct.Component.Key, RoleReferenceLocation> components) {
            return EquipmentSlot.CODEC.xmap(EquipmentSlotProperty::new,
                    EquipmentSlotProperty::slot);
        }
    }
}
