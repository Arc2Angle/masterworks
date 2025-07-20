package com.masterarms.masterarms;

import org.slf4j.Logger;

import com.masterarms.masterarms.material.Material;
import com.masterarms.masterarms.part.component.MaterialComponent;
import com.mojang.logging.LogUtils;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.NewRegistryEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Masterarms.MODID)
public class Masterarms {
        public static final String MODID = "masterarms";
        public static final Logger LOGGER = LogUtils.getLogger();

        public static final ResourceKey<Registry<Material>> MATERIALS_REGISTRY_KEY = ResourceKey
                        .createRegistryKey(ResourceLocation.fromNamespaceAndPath(MODID, "materials"));

        public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
        public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
        public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
                        .create(Registries.CREATIVE_MODE_TAB, MODID);
        public static final DeferredRegister<Material> MATERIALS = DeferredRegister.create(MATERIALS_REGISTRY_KEY,
                        MODID);

        public Masterarms(IEventBus modEventBus, ModContainer modContainer) {
                BLOCKS.register(modEventBus);
                ITEMS.register(modEventBus);
                CREATIVE_MODE_TABS.register(modEventBus);
                MATERIALS.register(modEventBus);

                modEventBus.addListener(this::registerRegistries);
                // NeoForge.EVENT_BUS.register(this);

                // Register our mod's ModConfigSpec so that FML can create and load the config
                // file for us
                modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        }

        public static Registry<Material> MATERIAL_STATS_REGISTRY;

        @SubscribeEvent
        public void registerRegistries(NewRegistryEvent event) {
                MATERIAL_STATS_REGISTRY = event
                                .create(new net.neoforged.neoforge.registries.RegistryBuilder<>(MATERIALS_REGISTRY_KEY)
                                                .sync(true));
        }
}
