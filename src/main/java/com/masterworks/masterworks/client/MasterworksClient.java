package com.masterworks.masterworks.client;

import com.masterworks.masterworks.MasterworksDataComponents;
import com.masterworks.masterworks.MasterworksItems;
import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.client.item.ConstructClientItemExtensions;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@Mod(value = MasterworksMod.ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = MasterworksMod.ID, value = Dist.CLIENT)
public class MasterworksClient {

    public MasterworksClient(ModContainer container, IEventBus bus) {
        com.masterworks.masterworks.MasterworksPreparableReloadListeners.register(bus);
        com.masterworks.masterworks.MasterworksSpecialModelRenderers.register(bus);

        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        event.getItemStack()
                .addToTooltip(
                        MasterworksDataComponents.CONSTRUCT.get(),
                        event.getContext(),
                        event.getToolTip()::add,
                        event.getFlags());
    }

    @SubscribeEvent
    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        ConstructClientItemExtensions extensions = new ConstructClientItemExtensions();
        Runtime.getRuntime().addShutdownHook(new Thread(extensions::close));

        event.registerItem(extensions, MasterworksItems.CONSTRUCT.get());
    }
}
