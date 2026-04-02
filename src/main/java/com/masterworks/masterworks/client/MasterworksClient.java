package com.masterworks.masterworks.client;

import com.masterworks.masterworks.MasterworksDataComponents;
import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.client.model.ConstructChestplateRenderLayer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@Mod(value = MasterworksMod.ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = MasterworksMod.ID, value = Dist.CLIENT)
public class MasterworksClient {

    public MasterworksClient(ModContainer container, IEventBus bus) {
        MasterworksReloadListeners.register(bus);
        MasterworksSpecialModelRenderers.register(bus);

        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        event.getItemStack()
                .addToTooltip(
                        MasterworksDataComponents.CONSTRUCT.get(),
                        event.getContext(),
                        TooltipDisplay.DEFAULT,
                        event.getToolTip()::add,
                        event.getFlags());
    }

    @SubscribeEvent
    public static void addPlayerRendererLayers(EntityRenderersEvent.AddLayers event) {
        for (PlayerModelType type : event.getSkins()) {
            AvatarRenderer<AbstractClientPlayer> playerRenderer = event.getPlayerRenderer(type);

            if (playerRenderer == null) {
                continue;
            }

            playerRenderer.addLayer(new ConstructChestplateRenderLayer(playerRenderer));
        }
    }
}
