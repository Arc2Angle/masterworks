package com.masterarms.masterarms;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;

@EventBusSubscriber(modid = masterarms.MODID)
public final class ComponentPatches {
    private static final DyedItemColor WOODEN_SWORD_DEFAULT_COLOR = new DyedItemColor(0);

    @SubscribeEvent
    public static void makeWoodenSwordDyeable(ModifyDefaultComponentsEvent e) {
        e.modify(Items.WOODEN_SWORD, b -> b.set(DataComponents.DYED_COLOR, WOODEN_SWORD_DEFAULT_COLOR));
    }
}