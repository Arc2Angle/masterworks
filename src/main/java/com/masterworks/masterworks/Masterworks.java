package com.masterworks.masterworks;

import org.slf4j.Logger;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import com.mojang.logging.LogUtils;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import com.masterworks.masterworks.component.*;
import com.masterworks.masterworks.item.*;
import com.masterworks.masterworks.datamap.*;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Masterworks.MOD_ID)
public class Masterworks {
    public static final String MOD_ID = "masterworks";
    public static final Logger LOGGER = LogUtils.getLogger();


    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final Supplier<CreativeModeTab> PARTS_TAB =
            CREATIVE_MODE_TABS.register("tool_parts",
                    () -> CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup." + MOD_ID + ".tool_parts"))
                            .icon(() -> new ItemStack(ToolPartItem.TOOL_PART.get()))
                            .displayItems((params, output) -> {
                                ToolPartItem.addAllPartItemStacks((itemStack) -> {
                                    output.accept(itemStack);
                                });

                            }).build());

    public Masterworks(IEventBus modEventBus, ModContainer modContainer) {
        // force class loading to ensure registrations happen
        TemplateItem.init();
        ToolPartItem.init();

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        DataComponents.register(modEventBus);

        modEventBus.addListener(this::registerDataMapTypes);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @SubscribeEvent
    public void registerDataMapTypes(RegisterDataMapTypesEvent event) {
        DataMaps.register(event);
    }

    public static ResourceLocation resourceLocation(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
