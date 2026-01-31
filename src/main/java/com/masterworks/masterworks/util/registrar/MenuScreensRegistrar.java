package com.masterworks.masterworks.util.registrar;

import com.mojang.datafixers.util.Unit;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class MenuScreensRegistrar {
    final String namespace;
    final List<Entry<?, ?>> entries = new LinkedList<>();

    public MenuScreensRegistrar(String namespace) {
        this.namespace = namespace;
    }

    public <T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> Unit registerMenuScreen(
            Supplier<? extends MenuType<? extends T>> menuType, MenuScreens.ScreenConstructor<T, U> screenConstructor) {
        entries.add(new Entry<>(menuType, screenConstructor));
        return Unit.INSTANCE;
    }

    public void register(IEventBus bus) {
        bus.addListener(this::addEntries);
    }

    void addEntries(RegisterMenuScreensEvent event) {
        for (var entry : entries) {
            entry.apply(event);
        }
    }

    record Entry<T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>>(
            Supplier<? extends MenuType<? extends T>> menuType, MenuScreens.ScreenConstructor<T, U> screenConstructor) {
        void apply(RegisterMenuScreensEvent event) {
            event.register(menuType.get(), screenConstructor);
        }
    }
}
