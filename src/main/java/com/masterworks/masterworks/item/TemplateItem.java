package com.masterworks.masterworks.item;

import java.util.Arrays;
import javax.annotation.Nonnull;
import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.data.Template;
import com.masterworks.masterworks.init.MasterworksDataComponents;
import com.masterworks.masterworks.init.MasterworksItems;
import com.masterworks.masterworks.resource.location.CompositionReferenceResourceLocation;
import com.masterworks.masterworks.resource.location.ShapeReferenceResourceLocation;
import com.masterworks.masterworks.resource.location.TierReferenceResourceLocation;
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

    public static ItemStack stack(Template template) {
        ItemStack stack = new ItemStack(MasterworksItems.TEMPLATE.get());
        stack.set(MasterworksDataComponents.TEMPLATE.get(), template);
        return stack;
    }

    public static class DisplayItemsGenerator implements CreativeModeTab.DisplayItemsGenerator {
        @Override
        public void accept(@Nonnull CreativeModeTab.ItemDisplayParameters params,
                @Nonnull CreativeModeTab.Output output) {
            output.accept(stack(template("basic", "rod", "rod")));
            output.accept(stack(template("basic", "binding", "binding")));
            output.accept(stack(template("basic", "pickaxe", "pickaxe")));
            output.accept(stack(template("basic", "pickaxe/head", "pickaxe/head")));
            output.accept(stack(template("basic", "sword", "sword/broad")));
            output.accept(stack(template("advanced", "sword/broad_blade", "sword/broad/blade",
                    "sword/broad/blade/dual")));
            output.accept(stack(
                    template("basic", "edge/straight/left", "sword/broad/blade/edge/straight")));
        }

        private static Template template(String tier, String shape, String... compositions) {
            return new Template(
                    TierReferenceResourceLocation.fromNamespaceAndPath(MasterworksMod.ID, tier),
                    ShapeReferenceResourceLocation.fromNamespaceAndPath(MasterworksMod.ID, shape),
                    Arrays.stream(compositions)
                            .map(composition -> CompositionReferenceResourceLocation
                                    .fromNamespaceAndPath(MasterworksMod.ID, composition))
                            .toList());
        }
    }
}
