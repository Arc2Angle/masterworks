package com.masterworks.masterworks;

import java.util.function.Supplier;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.data.property.core.ArmorProperty;
import com.masterworks.masterworks.data.property.core.AttackDamageProperty;
import com.masterworks.masterworks.data.property.core.AttackSpeedProperty;
import com.masterworks.masterworks.data.property.core.DefaultProperty;
import com.masterworks.masterworks.data.property.core.DurabilityProperty;
import com.masterworks.masterworks.data.property.core.MiningSpeedProperty;
import com.masterworks.masterworks.data.property.core.RenderProperty;
import com.masterworks.masterworks.data.property.core.ToughnessProperty;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MasterworksPropertyTypes {
    static final DeferredRegister<Property.Type<?>> REGISTRAR =
            DeferredRegister.create(MasterworksRegistries.PROPERTY_TYPE, MasterworksMod.ID);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    static <P extends Property, T extends Property.Type<P>> Supplier<T> register(String path,
            Supplier<T> type) {
        return REGISTRAR.register(path, type);
    }



    public static final Supplier<DefaultProperty.Type> DEFAULT =
            register("default", DefaultProperty.Type::new);

    public static final Supplier<RenderProperty.Type> RENDER =
            register("render", RenderProperty.Type::new);

    public static final Supplier<DurabilityProperty.Type> DURABILITY =
            register("durability", DurabilityProperty.Type::new);

    public static final Supplier<MiningSpeedProperty.Type> MINING_SPEED =
            register("mining_speed", MiningSpeedProperty.Type::new);

    public static final Supplier<AttackDamageProperty.Type> ATTACK_DAMAGE =
            register("attack_damage", AttackDamageProperty.Type::new);

    public static final Supplier<AttackSpeedProperty.Type> ATTACK_SPEED =
            register("attack_speed", AttackSpeedProperty.Type::new);

    public static final Supplier<ArmorProperty.Type> ARMOR =
            register("armor", ArmorProperty.Type::new);

    public static final Supplier<ToughnessProperty.Type> TOUGHNESS =
            register("toughness", ToughnessProperty.Type::new);
}
