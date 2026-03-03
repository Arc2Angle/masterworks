package com.masterworks.masterworks.item;

import com.masterworks.masterworks.MasterworksItems;
import javax.annotation.Nonnull;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Template items that serve as the base items for part types. They are currently placeholders that
 * lack functionality; this is intended.
 */
public class TemplateItem extends Item {
    public TemplateItem(Item.Properties properties) {
        super(properties);
    }

    public static class DisplayItemsGenerator implements CreativeModeTab.DisplayItemsGenerator {
        @Override
        public void accept(
                @Nonnull CreativeModeTab.ItemDisplayParameters params, @Nonnull CreativeModeTab.Output output) {
            output.accept(new ItemStack(MasterworksItems.ROD_TEMPLATE.get()));
            output.accept(new ItemStack(MasterworksItems.BINDING_TEMPLATE.get()));
            output.accept(new ItemStack(MasterworksItems.PICKAXE_HEAD_TEMPLATE.get()));
            output.accept(new ItemStack(MasterworksItems.PICKAXE_TEMPLATE.get()));
            output.accept(new ItemStack(MasterworksItems.BROAD_STRAIGHT_EDGE_TEMPLATE.get()));
            output.accept(new ItemStack(MasterworksItems.BROAD_SIRRATED_EDGE_TEMPLATE.get()));
            output.accept(new ItemStack(MasterworksItems.BROAD_BLADE_TEMPLATE.get()));
            output.accept(new ItemStack(MasterworksItems.BROADSWORD_TEMPLATE.get()));

            // TODO: add data components
        }
    }
}
