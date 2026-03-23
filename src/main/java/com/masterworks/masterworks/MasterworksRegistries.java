package com.masterworks.masterworks;

import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.data.role.Role;
import com.masterworks.masterworks.util.Registrar;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class MasterworksRegistries {
    private static Registrar<NewRegistryEvent> REGISTRAR = new Registrar<>(MasterworksMod.ID, NewRegistryEvent.class);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    private static <T> Registry<T> register(String path) {
        return REGISTRAR
                .addIdentified(path, id -> REGISTRAR.new Wrapper<Registry<T>>() {
                    private final Registry<T> registry =
                            new RegistryBuilder<>(ResourceKey.<T>createRegistryKey(id)).create();

                    @Override
                    public void accept(NewRegistryEvent event) {
                        event.register(registry);
                    }

                    @Override
                    public Registry<T> unwrap() {
                        return registry;
                    }
                })
                .unwrap();
    }

    public static final Registry<Property.Type<?>> PROPERTY_TYPE = register("property_type");

    public static final Registry<Property.Applier> PROPERTY_APPLIER = register("property_applier");

    public static final Registry<Role.Type<?>> ROLE_TYPE = register("role_type");
}
