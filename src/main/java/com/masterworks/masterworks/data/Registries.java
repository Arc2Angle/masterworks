package com.masterworks.masterworks.data;

import com.masterworks.masterworks.Masterworks;
import com.masterworks.masterworks.data.composition.Stat;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class Registries {
    public static void init() {}

    public static final ResourceKey<Registry<Stat>> STAT_KEY =
            ResourceKey.createRegistryKey(Masterworks.resourceLocation("stat"));

    public static final Registry<Stat> STAT = new RegistryBuilder<>(STAT_KEY).create();

    @SubscribeEvent
    public static void registers(NewRegistryEvent event) {

        for (Stat stat : Stat.values()) {
            Registry.register(STAT, stat.name, stat);
        }
        event.register(STAT);
    }
}
