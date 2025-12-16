package com.masterworks.masterworks.init;

import java.util.function.Supplier;
import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.data.property.RenderProperty;
import com.masterworks.masterworks.data.property.core.ArmorProperty;
import com.masterworks.masterworks.data.property.core.AttackDamageProperty;
import com.masterworks.masterworks.data.property.core.AttackSpeedProperty;
import com.masterworks.masterworks.data.property.core.DurabilityProperty;
import com.masterworks.masterworks.data.property.core.MiningSpeedProperty;
import com.masterworks.masterworks.data.property.core.ToughnessProperty;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MasterworksPropertyTypes {
    static final DeferredRegister<Property.Type<?>> REGISTRAR =
            DeferredRegister.create(MasterworksRegistries.PROPERTY_TYPE, MasterworksMod.ID);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    static <T extends Property> Supplier<Property.Type<T>> register(String path,
            Supplier<Property.Type<T>> type) {
        return REGISTRAR.register(path, type);
    }



    public static final Supplier<Property.Type<RenderProperty>> RENDER =
            register("render", RenderProperty.Type::new);

    public static final Supplier<Property.Type<DurabilityProperty>> DURABILITY =
            register("durability", DurabilityProperty.Type::new);

    public static final Supplier<Property.Type<MiningSpeedProperty>> MINING_SPEED =
            register("mining_speed", MiningSpeedProperty.Type::new);

    public static final Supplier<Property.Type<AttackDamageProperty>> ATTACK_DAMAGE =
            register("attack_damage", AttackDamageProperty.Type::new);

    public static final Supplier<Property.Type<AttackSpeedProperty>> ATTACK_SPEED =
            register("attack_speed", AttackSpeedProperty.Type::new);

    public static final Supplier<Property.Type<ArmorProperty>> ARMOR =
            register("armor", ArmorProperty.Type::new);

    public static final Supplier<Property.Type<ToughnessProperty>> TOUGHNESS =
            register("toughness", ToughnessProperty.Type::new);
}
