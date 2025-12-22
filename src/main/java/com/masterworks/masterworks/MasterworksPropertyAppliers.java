package com.masterworks.masterworks;

import java.util.function.Supplier;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.data.property.base.DataComponentProperty;
import com.masterworks.masterworks.data.property.base.ItemAttributeModifierProperty;
import com.masterworks.masterworks.data.property.base.LoreComponentProperty;
import com.masterworks.masterworks.data.property.base.ToolRuleProperty;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MasterworksPropertyAppliers {
    static final DeferredRegister<Property.Applier> REGISTRAR =
            DeferredRegister.create(MasterworksRegistries.PROPERTY_APPLIER, MasterworksMod.ID);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    static <T extends Property.Applier> Supplier<T> register(String path, Supplier<T> type) {
        return REGISTRAR.register(path, type);
    }



    public static final Supplier<DataComponentProperty.Applier> DATA_COMPONENT =
            register("data_component", DataComponentProperty.Applier::new);

    public static final Supplier<LoreComponentProperty.Applier> LORE_COMPONENT =
            register("lore_component", LoreComponentProperty.Applier::new);

    public static final Supplier<ItemAttributeModifierProperty.Applier> ITEM_ATTRIBUTE_MODIFIER =
            register("item_attribute_modifier", ItemAttributeModifierProperty.Applier::new);

    public static final Supplier<ToolRuleProperty.Applier> TOOL_RULE =
            register("tool_rule", ToolRuleProperty.Applier::new);
}
