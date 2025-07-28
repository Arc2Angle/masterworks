package com.masterworks.masterworks.data;

import com.masterworks.masterworks.Masterworks;
import com.masterworks.masterworks.properties.Stat;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class Registries {

    public static final ResourceKey<Registry<Stat>> STAT_KEY =
            ResourceKey.createRegistryKey(Masterworks.resourceLocation("stat"));

    public static final Registry<Stat> STAT = new RegistryBuilder<>(STAT_KEY).create();

    @SubscribeEvent
    public static void registers(NewRegistryEvent event) {
        event.register(STAT);
    }
}
