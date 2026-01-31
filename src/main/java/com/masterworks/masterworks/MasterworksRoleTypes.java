package com.masterworks.masterworks;

import com.masterworks.masterworks.data.role.ComponentRole;
import com.masterworks.masterworks.data.role.ItemRole;
import com.masterworks.masterworks.data.role.MaterialRole;
import com.masterworks.masterworks.data.role.Role;
import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MasterworksRoleTypes {

    static final DeferredRegister<Role.Type<?>> REGISTRAR =
            DeferredRegister.create(MasterworksRegistries.ROLE_TYPE, MasterworksMod.ID);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    static <T extends Role> Supplier<Role.Type<T>> register(String path, MapCodec<T> codec) {
        return REGISTRAR.register(path, () -> new Role.Type<>(codec));
    }

    public static final Supplier<Role.Type<MaterialRole>> MATERIAL = register("material", MaterialRole.CODEC);

    public static final Supplier<Role.Type<ComponentRole>> COMPONENT = register("component", ComponentRole.CODEC);

    public static final Supplier<Role.Type<ItemRole>> ITEM = register("item", ItemRole.CODEC);
}
