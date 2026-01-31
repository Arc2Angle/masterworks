package com.masterworks.masterworks;

import com.masterworks.masterworks.item.ConstructItem;
import com.masterworks.masterworks.item.TemplateItem;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MasterworksCreativeTabs {
    static final DeferredRegister<CreativeModeTab> REGISTRAR =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MasterworksMod.ID);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    static Supplier<CreativeModeTab> register(
            String name, Supplier<? extends ItemLike> icon, CreativeModeTab.DisplayItemsGenerator generator) {
        return REGISTRAR.register(name, key -> CreativeModeTab.builder()
                .title(Component.translatable(String.join(".", "itemGroup", key.getNamespace(), name)))
                .icon(() -> new ItemStack(icon.get()))
                .displayItems(generator)
                .build());
    }

    @SafeVarargs
    static Supplier<CreativeModeTab> register(
            String name, Supplier<? extends ItemLike> icon, Supplier<? extends ItemLike>... items) {
        return register(name, icon, new CollectionDisplayItemsGenerator(Arrays.asList(items)));
    }

    public static final Supplier<CreativeModeTab> TEMPLATES =
            register("templates", MasterworksItems.TEMPLATE, new TemplateItem.DisplayItemsGenerator());

    public static final Supplier<CreativeModeTab> CONSTRUCTS =
            register("constructs", MasterworksItems.CONSTRUCT, new ConstructItem.DisplayItemsGenerator());

    public static final Supplier<CreativeModeTab> OTHER =
            register("other", MasterworksItems.CONSTRUCT_FORGE, MasterworksItems.CONSTRUCT_FORGE);

    record CollectionDisplayItemsGenerator(Collection<? extends Supplier<? extends ItemLike>> items)
            implements CreativeModeTab.DisplayItemsGenerator {
        @Override
        public void accept(
                @Nonnull CreativeModeTab.ItemDisplayParameters parameters, @Nonnull CreativeModeTab.Output output) {
            for (Supplier<? extends ItemLike> item : items) {
                output.accept(item.get());
            }
        }
    }
}
