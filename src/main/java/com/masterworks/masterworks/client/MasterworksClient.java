package com.masterworks.masterworks.client;

import com.masterworks.masterworks.MasterworksMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = MasterworksMod.ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = MasterworksMod.ID, value = Dist.CLIENT)
public class MasterworksClient {

    public MasterworksClient(ModContainer container, IEventBus bus) {
        com.masterworks.masterworks.init.MasterworksSpecialModelRenderers.register(bus);

        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
