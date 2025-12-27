package com.masterworks.masterworks.data.property.base;

import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.MasterworksPropertyTypes;
import com.masterworks.masterworks.MasterworksTags;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.data.property.core.EquipmentSlotProperty;
import com.masterworks.masterworks.location.RoleReferenceLocation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.Equippable;

public interface EquippableProperty extends Property {
    void setEquippableValue(Construct construct, Equippable.Builder builder);

    @Override
    Type<?> type();

    interface Type<P extends EquippableProperty> extends Property.Type<P> {
    }

    class Applier extends Property.Applier {
        static final ResourceKey<EquipmentAsset> ASSET_KEY =
                ResourceKey.create(EquipmentAssets.ROOT_ID,
                        ResourceLocation.fromNamespaceAndPath(MasterworksMod.ID, "construct"));

        @Override
        public void apply(Construct construct, ItemStack stack) {
            EquipmentSlotProperty equipmentSlotProperty =
                    construct.properties(RoleReferenceLocation.ITEM)
                            .get(MasterworksPropertyTypes.EQUIPMENT_SLOT.get()).orElse(null);

            if (equipmentSlotProperty == null) {
                if (propertiesByTagKey(MasterworksTags.EQUIPPABLE_PROPERTY_TYPES, construct)
                        .count() > 1) {
                    MasterworksMod.LOGGER.warn(
                            "Construct {} has multiple EquippableProperties but is missing an EquipmentSlotProperty; cannot determine equipment slot for equippable",
                            construct);
                }
                return;
            }

            Equippable.Builder builder =
                    Equippable.builder(equipmentSlotProperty.slot()).setAsset(ASSET_KEY);

            propertiesByTagKey(MasterworksTags.EQUIPPABLE_PROPERTY_TYPES, construct)
                    .forEach(property -> property.setEquippableValue(construct, builder));

            stack.set(DataComponents.EQUIPPABLE, builder.build());
        }
    }
}
