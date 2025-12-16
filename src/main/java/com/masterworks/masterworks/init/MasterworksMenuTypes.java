package com.masterworks.masterworks.init;

import java.util.function.Supplier;
import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.gui.menu.ConstructForgeContainerMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MasterworksMenuTypes {
    static final DeferredRegister<MenuType<?>> REGISTRAR =
            DeferredRegister.create(Registries.MENU, MasterworksMod.ID);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    static <T extends AbstractContainerMenu> Supplier<MenuType<T>> register(String name,
            MenuType.MenuSupplier<T> factory) {
        return REGISTRAR.register(name, () -> new MenuType<>(factory, FeatureFlags.DEFAULT_FLAGS));
    }



    public static final Supplier<MenuType<ConstructForgeContainerMenu>> CONSTRUCT_FORGE =
            register("construct_forge", ConstructForgeContainerMenu::new);
}
