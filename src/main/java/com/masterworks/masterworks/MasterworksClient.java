package com.masterworks.masterworks;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import com.masterworks.masterworks.client.ClientEvents;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = Masterworks.MOD_ID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods
// in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = Masterworks.MOD_ID, value = Dist.CLIENT)
public class MasterworksClient {
    public MasterworksClient(ModContainer container, IEventBus modEventBus) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your
        // mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json
        // file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        // Register client-side events for dynamic item rendering
        modEventBus.addListener(ClientEvents::registerSelectItemModelProperties);
        modEventBus.addListener(ClientEvents::registerItemTintSources);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {}
}
