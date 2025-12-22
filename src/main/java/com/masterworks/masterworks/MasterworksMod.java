package com.masterworks.masterworks;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;


// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(MasterworksMod.ID)
@EventBusSubscriber(modid = MasterworksMod.ID)
public class MasterworksMod {

    public static final String ID = "masterworks";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MasterworksMod(IEventBus bus, ModContainer container) {
        com.masterworks.masterworks.init.MasterworksRegistries.register(bus);
        com.masterworks.masterworks.init.MasterworksDataPackRegistries.register(bus);

        com.masterworks.masterworks.init.MasterworksDataMapTypes.register(bus);
        com.masterworks.masterworks.init.MasterworksDataComponents.register(bus);
        com.masterworks.masterworks.init.MasterworksMenuTypes.register(bus);
        com.masterworks.masterworks.init.MasterworksMenuScreens.register(bus);
        com.masterworks.masterworks.init.MasterworksItems.register(bus);
        com.masterworks.masterworks.init.MasterworksBlocks.register(bus);
        com.masterworks.masterworks.init.MasterworksBlockEntityTypes.register(bus);
        com.masterworks.masterworks.init.MasterworksCreativeTabs.register(bus);
        com.masterworks.masterworks.init.MasterworksRecipeSerializers.register(bus);
        com.masterworks.masterworks.init.MasterworksRecipeTypes.register(bus);
        com.masterworks.masterworks.init.MasterworksIngredientTypes.register(bus);
        com.masterworks.masterworks.init.MasterworksCapabilities.register(bus);

        com.masterworks.masterworks.init.MasterworksPropertyTypes.register(bus);
        com.masterworks.masterworks.init.MasterworksPropertyAppliers.register(bus);
        com.masterworks.masterworks.init.MasterworksRoleTypes.register(bus);

        container.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
