package com.masterworks.masterworks.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
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

    // Template items for different part types
    public static final DeferredHolder<Item, TemplateItem> BLADE_TEMPLATE =
            Masterworks.ITEMS.registerItem("blade_template", TemplateItem::new);

    public static final DeferredHolder<Item, TemplateItem> HANDLE_TEMPLATE =
            Masterworks.ITEMS.registerItem("handle_template", TemplateItem::new);

    public static final DeferredHolder<Item, TemplateItem> BINDING_TEMPLATE =
            Masterworks.ITEMS.registerItem("binding_template", TemplateItem::new);
}
