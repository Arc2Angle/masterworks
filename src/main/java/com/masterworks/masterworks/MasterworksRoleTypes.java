package com.masterworks.masterworks;

import com.masterworks.masterworks.data.role.CompositorRole;
import com.masterworks.masterworks.data.role.MaterializerRole;
import com.masterworks.masterworks.data.role.Role;
import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MasterworksRoleTypes {
    private static final DeferredRegister<Role.Type<?>> REGISTRAR =
            DeferredRegister.create(MasterworksRegistries.ROLE_TYPE, MasterworksMod.ID);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    private static <T extends Role> Supplier<Role.Type<T>> register(String path, MapCodec<T> codec) {
        return REGISTRAR.register(path, () -> new Role.Type<>(codec));
    }

    public static final Supplier<Role.Type<MaterializerRole>> MATERIALIZER =
            register("materializer", MaterializerRole.CODEC);

    public static final Supplier<Role.Type<CompositorRole>> COMPOSITOR = register("compositor", CompositorRole.CODEC);
}
