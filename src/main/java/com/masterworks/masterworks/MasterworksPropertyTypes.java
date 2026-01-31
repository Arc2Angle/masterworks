package com.masterworks.masterworks;

import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.data.property.core.ArmorProperty;
import com.masterworks.masterworks.data.property.core.AttackDamageProperty;
import com.masterworks.masterworks.data.property.core.AttackSpeedProperty;
import com.masterworks.masterworks.data.property.core.DurabilityProperty;
import com.masterworks.masterworks.data.property.core.EquipmentSlotProperty;
import com.masterworks.masterworks.data.property.core.EquipmentSwappableProperty;
import com.masterworks.masterworks.data.property.core.MaxStackSizeProperty;
import com.masterworks.masterworks.data.property.core.MiningDeniedProperty;
import com.masterworks.masterworks.data.property.core.MiningSpeedProperty;
import com.masterworks.masterworks.data.property.core.RenderEquipmentProperty;
import com.masterworks.masterworks.data.property.core.RenderItemProperty;
import com.masterworks.masterworks.data.property.core.ToughnessProperty;
import java.util.function.Supplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MasterworksPropertyTypes {
    static final DeferredRegister<Property.Type<?>> REGISTRAR =
            DeferredRegister.create(MasterworksRegistries.PROPERTY_TYPE, MasterworksMod.ID);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    static <P extends Property, T extends Property.Type<P>> Supplier<T> register(String path, Supplier<T> type) {
        return REGISTRAR.register(path, type);
    }

    public static final Supplier<RenderItemProperty.Type> RENDER_ITEM =
            register("render_item", RenderItemProperty.Type::new);

    public static final Supplier<RenderEquipmentProperty.Type> RENDER_EQUIPMENT =
            register("render_equipment", RenderEquipmentProperty.Type::new);

    public static final Supplier<MaxStackSizeProperty.Type> MAX_STACK_SIZE =
            register("max_stack_size", MaxStackSizeProperty.Type::new);

    public static final Supplier<DurabilityProperty.Type> DURABILITY =
            register("durability", DurabilityProperty.Type::new);

    public static final Supplier<MiningDeniedProperty.Type> MINING_DENIED =
            register("mining_denied", MiningDeniedProperty.Type::new);

    public static final Supplier<MiningSpeedProperty.Type> MINING_SPEED =
            register("mining_speed", MiningSpeedProperty.Type::new);

    public static final Supplier<AttackDamageProperty.Type> ATTACK_DAMAGE =
            register("attack_damage", AttackDamageProperty.Type::new);

    public static final Supplier<AttackSpeedProperty.Type> ATTACK_SPEED =
            register("attack_speed", AttackSpeedProperty.Type::new);

    public static final Supplier<ArmorProperty.Type> ARMOR = register("armor", ArmorProperty.Type::new);

    public static final Supplier<ToughnessProperty.Type> TOUGHNESS = register("toughness", ToughnessProperty.Type::new);

    public static final Supplier<EquipmentSlotProperty.Type> EQUIPMENT_SLOT =
            register("equipment_slot", EquipmentSlotProperty.Type::new);

    public static final Supplier<EquipmentSwappableProperty.Type> EQUIPMENT_SWAPPABLE =
            register("equipment_swappable", EquipmentSwappableProperty.Type::new);
}
