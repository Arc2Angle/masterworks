package com.masterworks.masterworks;

import org.slf4j.Logger;

import net.minecraft.network.chat.Component;
import com.masterworks.masterworks.component.DataComponents;
import com.masterworks.masterworks.material.Material;
import com.masterworks.masterworks.part.type.PartType;
import com.mojang.logging.LogUtils;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.neoforged.neoforge.registries.NewRegistryEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Masterworks.MOD_ID)
public class Masterworks {
        public static final String MOD_ID = "masterworks";
        public static final Logger LOGGER = LogUtils.getLogger();

        public static final ResourceKey<Registry<Material>> MATERIALS_REGISTRY_KEY =
                        ResourceKey.createRegistryKey(
                                        ResourceLocation.fromNamespaceAndPath(MOD_ID, "materials"));
        public static final ResourceKey<Registry<PartType>> PART_TYPES_REGISTRY_KEY =
                        ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MOD_ID,
                                        "part_types"));

        public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
        public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
        public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
                        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

        public static final DeferredRegister<Material> MATERIALS =
                        DeferredRegister.create(MATERIALS_REGISTRY_KEY, MOD_ID);
        public static final DeferredRegister<PartType> PART_TYPES =
                        DeferredRegister.create(PART_TYPES_REGISTRY_KEY, MOD_ID);

        public static final Supplier<CreativeModeTab> PARTS_TAB = CREATIVE_MODE_TABS.register(
                        "parts",
                        () -> CreativeModeTab.builder()
                                        .title(Component.translatable(
                                                        "itemGroup." + MOD_ID + ".parts"))
                                        .icon(() -> new ItemStack(
                                                        com.masterworks.masterworks.item.Items.PART
                                                                        .get()))
                                        .displayItems((params, output) -> {
                                                com.masterworks.masterworks.item.Items
                                                                .addAllParts((itemStack) -> {
                                                                        output.accept(itemStack);
                                                                });

                                        }).build());

        public Masterworks(IEventBus modEventBus, ModContainer modContainer) {
                // force class loading to ensure registrations happen
                com.masterworks.masterworks.material.Materials.init();
                com.masterworks.masterworks.part.type.PartTypes.init();
                com.masterworks.masterworks.item.Items.init();

                BLOCKS.register(modEventBus);
                ITEMS.register(modEventBus);
                CREATIVE_MODE_TABS.register(modEventBus);

                MATERIALS.register(modEventBus);
                PART_TYPES.register(modEventBus);
                DataComponents.register(modEventBus);

                modEventBus.addListener(this::registerRegistries);
                // NeoForge.EVENT_BUS.register(this);

                // Register our mod's ModConfigSpec so that FML can create and load the config
                // file for us
                modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        }

        public static Registry<Material> MATERIALS_REGISTRY;
        public static Registry<PartType> PART_TYPES_REGISTRY;

        @SubscribeEvent
        public void registerRegistries(NewRegistryEvent event) {
                MATERIALS_REGISTRY = event
                                .create(new RegistryBuilder<>(MATERIALS_REGISTRY_KEY).sync(true));
                PART_TYPES_REGISTRY = event
                                .create(new RegistryBuilder<>(PART_TYPES_REGISTRY_KEY).sync(true));
        }
}
