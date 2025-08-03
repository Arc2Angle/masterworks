package com.masterworks.masterworks.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import java.util.function.Supplier;
import com.masterworks.masterworks.Masterworks;

/**
 * Template items that serve as the base items for part types. They are currently placeholders that
 * lack functionality; this is intended.
 */
public class TemplateItem extends Item {

    public static void init() {}

    private TemplateItem(Item.Properties properties) {
        super(properties);
    }

    public static final DeferredHolder<Item, TemplateItem> HANDLE_TEMPLATE =
            Masterworks.ITEMS.registerItem("rod_template", TemplateItem::new);

    public static final DeferredHolder<Item, TemplateItem> BINDING_TEMPLATE =
            Masterworks.ITEMS.registerItem("binding_template", TemplateItem::new);

    public static final DeferredHolder<Item, TemplateItem> PICKAXE_HEAD_TEMPLATE =
            Masterworks.ITEMS.registerItem("pickaxe_head_template", TemplateItem::new);

    public static final DeferredHolder<Item, TemplateItem> PICKAXE_TEMPLATE =
            Masterworks.ITEMS.registerItem("pickaxe_template", TemplateItem::new);

    public static final DeferredHolder<Item, TemplateItem> SWORD_BLADE_TEMPLATE =
            Masterworks.ITEMS.registerItem("sword_blade_template", TemplateItem::new);

    public static final DeferredHolder<Item, TemplateItem> SWORD_TEMPLATE =
            Masterworks.ITEMS.registerItem("sword_template", TemplateItem::new);


    public static final Supplier<CreativeModeTab> TEMPLATES_TAB =
            Masterworks.CREATIVE_MODE_TABS.register("templates", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + Masterworks.MOD_ID + ".templates"))
                    .icon(() -> new ItemStack(PICKAXE_TEMPLATE.get()))
                    .displayItems((params, output) -> {
                        output.accept(HANDLE_TEMPLATE.get());
                        output.accept(BINDING_TEMPLATE.get());
                        output.accept(PICKAXE_HEAD_TEMPLATE.get());
                        output.accept(PICKAXE_TEMPLATE.get());
                        output.accept(SWORD_BLADE_TEMPLATE.get());
                        output.accept(SWORD_TEMPLATE.get());
                    }).build());
}
