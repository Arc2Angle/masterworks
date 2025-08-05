package com.masterworks.masterworks.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.neoforge.registries.DeferredHolder;
import javax.annotation.Nonnull;
import com.masterworks.masterworks.Masterworks;
import com.masterworks.masterworks.data.composition.PartDefinition;
import com.masterworks.masterworks.data.construct.Construct;
import com.masterworks.masterworks.data.material.Material;
import com.masterworks.masterworks.resource.location.MaterialResourceLocation;
import com.masterworks.masterworks.resource.location.TemplateResourceLocation;
import com.masterworks.masterworks.util.streams.BiStream;
import com.mojang.datafixers.util.Either;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConstructItem extends Item {

    public static void init() {}

    private ConstructItem(Item.Properties properties) {
        super(properties);
    }

    public static final DeferredHolder<Item, ConstructItem> ITEM =
            Masterworks.ITEMS.registerItem("construct", ConstructItem::new);

    public static final Supplier<CreativeModeTab> PARTS_TAB =
            Masterworks.CREATIVE_MODE_TABS.register("constructs", () -> CreativeModeTab.builder()
                    .title(Component
                            .translatable("itemGroup." + Masterworks.MOD_ID + ".constructs"))
                    .icon(() -> new ItemStack(ConstructItem.ITEM.get()))
                    .displayItems(new DisplayItemsGenerator()).build());

    /**
     * Creates a configured ItemStack with the specified material and part type items. This is the
     * only way to create a valid ConstructItem stack.
     */
    public static ItemStack create(Item item, Construct construct) {
        ItemStack stack = new ItemStack(item);
        stack.set(Construct.DATA_COMPONENT.get(), construct);
        return stack;
    }

    /**
     * Adds the tooltip information for this item.
     * 
     * Note: `appendHoverText` is currently deprecated, but no other solution exists.
     */
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull Item.TooltipContext context,
            @Nonnull TooltipDisplay display, @Nonnull Consumer<Component> adder,
            @Nonnull TooltipFlag flag) {
        super.appendHoverText(stack, context, display, adder, flag);

        Construct construct = stack.get(Construct.DATA_COMPONENT.get());

        if (construct == null) {
            adder.accept(Component.literal("Broken Construct")
                    .withStyle(style -> style.withColor(0xFF0000)));
            return;
        }

        adder.accept(formatConstruct(construct));
    }

    private static MutableComponent formatConstruct(Construct construct) {
        var partComponents = BiStream
                .zip(construct.parts().stream(), construct.getComposition().parts().stream())
                .map((materialOrPart, definition) -> materialOrPart
                        .map(ConstructItem::formatMaterial, ConstructItem::formatConstruct)
                        .append(formatPartDefinition(definition)))
                .toList();

        if (partComponents.size() == 1) {
            return partComponents.get(0);
        }

        var partsChainComponent = partComponents
                .stream().reduce((left, right) -> left
                        .append(Component.literal(" + ").withColor(0xFFFFFF)).append(right))
                .orElse(Component.empty());

        return Component.literal("(").withColor(0xFFFFFF).append(partsChainComponent)
                .append(Component.literal(")").withColor(0xFFFFFF));
    }

    private static MutableComponent formatMaterial(MaterialResourceLocation resourceLocation) {
        Material material = resourceLocation.getMappedValue();
        return Component.literal(material.name()).withColor(material.interfaceColor().argb());
    }

    private static MutableComponent formatPartDefinition(PartDefinition definition) {
        return definition.identifier()
                .map(identifier -> Component.literal(" "
                        + Character.toUpperCase(identifier.charAt(0)) + identifier.substring(1)))
                .orElse(Component.empty());
    }


    private static class DisplayItemsGenerator implements CreativeModeTab.DisplayItemsGenerator {
        private static final TemplateResourceLocation rodTemplate =
                TemplateResourceLocation.fromMasterworksAndPath("rod_template");
        private static final TemplateResourceLocation bindingTemplate =
                TemplateResourceLocation.fromMasterworksAndPath("binding_template");
        private static final TemplateResourceLocation pickaxeHeadTemplate =
                TemplateResourceLocation.fromMasterworksAndPath("pickaxe_head_template");
        private static final TemplateResourceLocation pickaxeTemplate =
                TemplateResourceLocation.fromMasterworksAndPath("pickaxe_template");
        private static final TemplateResourceLocation swordBladeTemplate =
                TemplateResourceLocation.fromMasterworksAndPath("sword_blade_template");
        private static final TemplateResourceLocation swordTemplate =
                TemplateResourceLocation.fromMasterworksAndPath("sword_template");

        private static final MaterialResourceLocation wood =
                MaterialResourceLocation.fromNamespaceAndPath("minecraft", "oak_planks");
        private static final MaterialResourceLocation stone =
                MaterialResourceLocation.fromNamespaceAndPath("minecraft", "stone");
        private static final MaterialResourceLocation iron =
                MaterialResourceLocation.fromNamespaceAndPath("minecraft", "iron_ingot");
        private static final MaterialResourceLocation gold =
                MaterialResourceLocation.fromNamespaceAndPath("minecraft", "gold_ingot");
        private static final MaterialResourceLocation diamond =
                MaterialResourceLocation.fromNamespaceAndPath("minecraft", "diamond");
        private static final MaterialResourceLocation emerald =
                MaterialResourceLocation.fromNamespaceAndPath("minecraft", "emerald");

        @Override
        public void accept(@Nonnull ItemDisplayParameters params, @Nonnull Output output) {
            Construct woodRod = simple(rodTemplate, wood);
            Construct ironRod = simple(rodTemplate, iron);
            Construct diamondRod = simple(rodTemplate, diamond);

            Construct stoneBinding = simple(bindingTemplate, stone);
            Construct ironBinding = simple(bindingTemplate, iron);
            Construct emeraldBinding = simple(bindingTemplate, emerald);

            Construct stonePickaxeHead = simple(pickaxeHeadTemplate, stone);
            Construct goldPickaxeHead = simple(pickaxeHeadTemplate, gold);

            Construct diamondSwordBlade = simple(swordBladeTemplate, diamond);
            Construct ironSwordBlade = simple(swordBladeTemplate, iron);
            Construct diamondIronSwordBlade =
                    composite(swordBladeTemplate, 1, diamondSwordBlade, ironSwordBlade);

            output.accept(stack(simple(pickaxeTemplate, emerald)));

            output.accept(stack(stonePickaxeHead));
            output.accept(stack(ironRod));
            output.accept(stack(emeraldBinding));
            output.accept(
                    stack(composite(pickaxeTemplate, 1, goldPickaxeHead, ironRod, emeraldBinding)));

            output.accept(stack(ironSwordBlade));
            output.accept(stack(diamondRod));
            output.accept(stack(stoneBinding));
            output.accept(
                    stack(composite(swordTemplate, 0, ironSwordBlade, diamondRod, stoneBinding)));

            output.accept(stack(diamondIronSwordBlade));
            output.accept(stack(woodRod));
            output.accept(stack(ironBinding));
            output.accept(stack(
                    composite(swordTemplate, 1, diamondIronSwordBlade, woodRod, ironBinding)));
        }

        private static Construct simple(TemplateResourceLocation template,
                MaterialResourceLocation material) {
            return new Construct(template, 0, List.of(Either.left(material)));
        }

        private static Construct composite(TemplateResourceLocation template, int variant,
                Construct... parts) {
            return new Construct(template, variant, Arrays.stream(parts)
                    .map(Either::<MaterialResourceLocation, Construct>right).toList());
        }

        private static ItemStack stack(Construct construct) {
            return ConstructItem.create(ConstructItem.ITEM.get(), construct);
        }
    }
}
