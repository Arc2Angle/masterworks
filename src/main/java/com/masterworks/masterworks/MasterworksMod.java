package com.masterworks.masterworks;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(MasterworksMod.ID)
@EventBusSubscriber(modid = MasterworksMod.ID)
public class MasterworksMod {

    public static final String ID = "masterworks";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MasterworksMod(IEventBus bus, ModContainer container) {
        MasterworksRegistries.register(bus);
        MasterworksDataPackRegistries.register(bus);

        MasterworksDataMapTypes.register(bus);
        MasterworksDataComponents.register(bus);
        MasterworksMenuTypes.register(bus);
        MasterworksMenuScreens.register(bus);
        MasterworksItems.register(bus);
        MasterworksBlocks.register(bus);
        MasterworksBlockEntityTypes.register(bus);
        MasterworksCreativeModeTabs.register(bus);
        MasterworksRecipeSerializers.register(bus);
        MasterworksRecipeTypes.register(bus);
        MasterworksIngredientTypes.register(bus);
        MasterworksCapabilities.register(bus);

        MasterworksPropertyTypes.register(bus);
        MasterworksPropertyAppliers.register(bus);
        MasterworksCompositionTypes.register(bus);

        container.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    /* Possible significant rework:
    Custom macro system for JSONs, allowing defining objects through interpreted strings. For example:
    {
        "a": "something/a",
        "b": "something/b",
        "c": "something/c",
    }
    Becomes:
    {
        "$foreach @x in a, b, c": "something/@x"
    }
    */
}
