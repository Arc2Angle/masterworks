package com.masterworks.masterworks;

import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.data.role.Role;
import com.masterworks.masterworks.util.registrar.RegistriesRegistrar;
import net.minecraft.core.Registry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class MasterworksRegistries {
    private static RegistriesRegistrar REGISTRAR = new RegistriesRegistrar(MasterworksMod.ID);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    private static <T> Registry<T> register(String name) {
        return REGISTRAR.registerRegistry(name, key -> new RegistryBuilder<T>(key).create());
    }

    public static final Registry<Property.Type<?>> PROPERTY_TYPE = register("property_type");

    public static final Registry<Property.Applier> PROPERTY_APPLIER = register("property_applier");

    public static final Registry<Role.Type<?>> ROLE_TYPE = register("role_type");
}
