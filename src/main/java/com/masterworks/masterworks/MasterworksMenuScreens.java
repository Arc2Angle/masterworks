package com.masterworks.masterworks;

import com.masterworks.masterworks.util.Registrar;
import java.util.function.Supplier;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class MasterworksMenuScreens {
    private static final Registrar<RegisterMenuScreensEvent> REGISTRAR =
            new Registrar<>(MasterworksMod.ID, RegisterMenuScreensEvent.class);

    @SuppressWarnings("unused")
    private static <T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> Void register(
            Supplier<? extends MenuType<? extends T>> menuType, MenuScreens.ScreenConstructor<T, U> constructor) {
        REGISTRAR.addUnidentified(event -> event.register(menuType.get(), constructor));
        return null;
    }

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }
}
