package com.masterworks.masterworks;

import java.util.function.Supplier;
import com.masterworks.masterworks.gui.screen.ConstructForgeContainerScreen;
import com.masterworks.masterworks.util.registrar.MenuScreensRegistrar;
import com.mojang.datafixers.util.Unit;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;

public class MasterworksMenuScreens {
    static final MenuScreensRegistrar REGISTRAR = new MenuScreensRegistrar(MasterworksMod.ID);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    static <T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> Unit register(
            Supplier<? extends MenuType<? extends T>> menuType,
            MenuScreens.ScreenConstructor<T, U> factory) {
        return REGISTRAR.registerMenuScreen(menuType, factory);
    }



    public static final Unit CONSTRUCT_FORGE =
            register(MasterworksMenuTypes.CONSTRUCT_FORGE, ConstructForgeContainerScreen::new);

}
