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
        com.masterworks.masterworks.MasterworksRegistries.register(bus);
        com.masterworks.masterworks.MasterworksDataPackRegistries.register(bus);

        com.masterworks.masterworks.MasterworksDataMapTypes.register(bus);
        com.masterworks.masterworks.MasterworksDataComponents.register(bus);
        com.masterworks.masterworks.MasterworksMenuTypes.register(bus);
        com.masterworks.masterworks.MasterworksMenuScreens.register(bus);
        com.masterworks.masterworks.MasterworksItems.register(bus);
        com.masterworks.masterworks.MasterworksBlocks.register(bus);
        com.masterworks.masterworks.MasterworksBlockEntityTypes.register(bus);
        com.masterworks.masterworks.MasterworksCreativeTabs.register(bus);
        com.masterworks.masterworks.MasterworksRecipeSerializers.register(bus);
        com.masterworks.masterworks.MasterworksRecipeTypes.register(bus);
        com.masterworks.masterworks.MasterworksIngredientTypes.register(bus);
        com.masterworks.masterworks.MasterworksCapabilities.register(bus);

        com.masterworks.masterworks.MasterworksPropertyTypes.register(bus);
        com.masterworks.masterworks.MasterworksPropertyAppliers.register(bus);
        com.masterworks.masterworks.MasterworksRoleTypes.register(bus);

        container.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
