package com.masterworks.masterworks;

import org.slf4j.Logger;

import net.minecraft.resources.ResourceLocation;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.masterworks.masterworks.item.ToolPartItem;
import com.masterworks.masterworks.item.TemplateItem;


// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Masterworks.MOD_ID)
public class Masterworks {
    public static final String MOD_ID = "masterworks";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation resourceLocation(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public Masterworks(IEventBus modEventBus, ModContainer modContainer) {
        // force class loading to ensure registrations happen
        com.masterworks.masterworks.data.Registries.init();
        com.masterworks.masterworks.data.Components.init();
        com.masterworks.masterworks.data.Maps.init();

        TemplateItem.init();
        ToolPartItem.init();

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

}
