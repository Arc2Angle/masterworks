package com.masterworks.masterworks.init;

import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.data.Composition;
import com.masterworks.masterworks.data.Material;
import com.masterworks.masterworks.data.role.Role;
import com.masterworks.masterworks.init.registrar.DataPackRegistriesRegistrar;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;

public class MasterworksDataPackRegistries {
    static DataPackRegistriesRegistrar REGISTRAR =
            new DataPackRegistriesRegistrar(MasterworksMod.ID);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    static <T> ResourceKey<Registry<T>> register(String name, Codec<T> codec) {
        return REGISTRAR.registerDataPackRegistry(name, codec);
    }



    public static final ResourceKey<Registry<Material>> MATERIAL =
            register("material", Material.CODEC);

    public static final ResourceKey<Registry<Role>> ROLE = register("role", Role.CODEC);

    public static final ResourceKey<Registry<Composition>> COMPOSITION =
            register("composition", Composition.CODEC);
}
