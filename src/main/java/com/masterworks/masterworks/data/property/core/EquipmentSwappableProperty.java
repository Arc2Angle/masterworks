package com.masterworks.masterworks.data.property.core;

import com.masterworks.masterworks.MasterworksPropertyTypes;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.base.EquippableProperty;
import com.masterworks.masterworks.location.RoleReferenceLocation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import java.util.Map;
import net.minecraft.world.item.equipment.Equippable.Builder;

public record EquipmentSwappableProperty(boolean value) implements EquippableProperty {
    @Override
    public void setEquippableValue(Construct construct, Builder builder) {
        builder.setSwappable(this.value);
    }

    @Override
    public Type type() {
        return MasterworksPropertyTypes.EQUIPMENT_SWAPPABLE.get();
    }

    public static final class Type implements EquippableProperty.Type<EquipmentSwappableProperty> {
        @Override
        public Decoder<EquipmentSwappableProperty> decoder(
                Map<Construct.Component.Key, RoleReferenceLocation> components) {
            return Codec.BOOL.xmap(EquipmentSwappableProperty::new, EquipmentSwappableProperty::value);
        }
    }
}
