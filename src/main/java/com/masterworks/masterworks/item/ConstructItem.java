package com.masterworks.masterworks.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import javax.annotation.Nonnull;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.Material;
import com.masterworks.masterworks.data.property.base.ExpressionProperty;
import com.masterworks.masterworks.data.property.base.ItemAttributeProperty;
import com.masterworks.masterworks.data.property.base.ToolRuleProperty;
import com.masterworks.masterworks.init.MasterworksDataComponents;
import com.masterworks.masterworks.init.MasterworksItems;
import com.masterworks.masterworks.init.MasterworksTags;
import com.masterworks.masterworks.resource.location.MaterialReferenceResourceLocation;
import com.masterworks.masterworks.resource.location.PropertyTypeReferenceResourceLocation;
import com.masterworks.masterworks.resource.location.RoleReferenceResourceLocation;
import com.masterworks.masterworks.resource.location.CompositionReferenceResourceLocation;
import com.mojang.datafixers.util.Either;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ConstructItem extends Item {
    public ConstructItem(Item.Properties properties) {
        super(properties);
    }

    public static ItemStack stack(Construct construct) {
        ItemStack stack = new ItemStack(MasterworksItems.CONSTRUCT.get());

        stack.set(MasterworksDataComponents.CONSTRUCT.get(), construct);
        stack.set(DataComponents.MAX_STACK_SIZE, 1);

        applyDataComponents(construct, stack);
        applyItemAttributes(construct, stack);
        applyToolRules(construct, stack);

        return stack;
    }

    static void applyDataComponents(Construct construct, ItemStack stack) {
        MasterworksTags.DATA_COMPONENT_PROPERTY_TYPES.values()
                .forEach(type -> type.apply(construct, stack));
    }

    static void applyItemAttributes(Construct construct, ItemStack stack) {
        ItemAttributeProperty.Builder builder = new ItemAttributeProperty.Builder();
        MasterworksTags.ITEM_ATTRIBUTE_PROPERTY_TYPES.values()
                .forEach(type -> builder.add(type, construct));
        builder.apply(stack);
    }

    static void applyToolRules(Construct construct, ItemStack stack) {
        ToolRuleProperty.Builder builder = new ToolRuleProperty.Builder();
        MasterworksTags.TOOL_RULE_PROPERTY_TYPES.values()
                .forEach(type -> builder.add(type, construct));
        builder.apply(stack);
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

        Construct construct = stack.get(MasterworksDataComponents.CONSTRUCT.get());

        if (construct == null) {
            adder.accept(Component.literal("Broken Construct")
                    .withStyle(style -> style.withColor(0xFF0000)));
            return;
        }

        adder.accept(formatConstruct(construct));

        PropertyTypeReferenceResourceLocation.all().forEach(reference -> {
            if (reference.registered().value() instanceof ExpressionProperty.Type<?> type) {
                try {
                    ExpressionProperty property =
                            construct.getPropertyOrThrow(type, RoleReferenceResourceLocation.ITEM);

                    Double value = property.evaluate(construct);

                    if (value != null) {
                        adder.accept(Component.literal(type.name() + ": " + value));
                    }
                } catch (Construct.PropertyAccessException e) {
                }
            }
        });
    }

    private static MutableComponent formatConstruct(Construct construct) {
        var components = construct.components();

        if (components.size() == 1) {
            return components.values().iterator().next().value().map(ConstructItem::formatMaterial,
                    ConstructItem::formatConstruct);
        }

        return formatWrapBraces(components.entrySet().stream().map(entry -> {
            return entry.getValue().value()
                    .map(ConstructItem::formatMaterial, ConstructItem::formatConstruct)
                    .append(formatComponentKey(entry.getKey()));
        }).reduce(ConstructItem::formatJoinPlus).orElse(Component.empty()));
    }

    private static MutableComponent formatMaterial(
            MaterialReferenceResourceLocation resourceLocation) {
        Material material = resourceLocation.registered().value();
        return Component.literal(material.name()).withColor(material.color().argb());
    }

    private static MutableComponent formatComponentKey(Construct.Component.Key key) {
        return Component.literal(
                " " + Character.toUpperCase(key.value().charAt(0)) + key.value().substring(1));
    }

    private static MutableComponent formatWrapBraces(Component value) {
        return Component.literal("(").withColor(0xFFFFFF).append(value)
                .append(Component.literal(")").withColor(0xFFFFFF));
    }

    private static MutableComponent formatJoinPlus(MutableComponent left, MutableComponent right) {
        return left.append(Component.literal(" + ").withColor(0xFFFFFF)).append(right);
    }


    public static class DisplayItemsGenerator implements CreativeModeTab.DisplayItemsGenerator {
        static final CompositionReferenceResourceLocation rod =
                CompositionReferenceResourceLocation.fromNamespaceAndPath("masterworks", "rod");
        static final CompositionReferenceResourceLocation binding =
                CompositionReferenceResourceLocation.fromNamespaceAndPath("masterworks", "binding");
        static final CompositionReferenceResourceLocation pickaxe =
                CompositionReferenceResourceLocation.fromNamespaceAndPath("masterworks", "pickaxe");
        static final CompositionReferenceResourceLocation pickaxeHead =
                CompositionReferenceResourceLocation.fromNamespaceAndPath("masterworks",
                        "pickaxe/head");
        static final CompositionReferenceResourceLocation broadSword =
                CompositionReferenceResourceLocation.fromNamespaceAndPath("masterworks",
                        "sword/broad");
        static final CompositionReferenceResourceLocation broadSwordBlade =
                CompositionReferenceResourceLocation.fromNamespaceAndPath("masterworks",
                        "sword/broad/blade");
        static final CompositionReferenceResourceLocation broadSwordBladeDual =
                CompositionReferenceResourceLocation.fromNamespaceAndPath("masterworks",
                        "sword/broad/blade/dual");
        static final CompositionReferenceResourceLocation broadSwordBladeStraightEdge =
                CompositionReferenceResourceLocation.fromNamespaceAndPath("masterworks",
                        "sword/broad/blade/edge/straight");

        static final MaterialReferenceResourceLocation wood =
                MaterialReferenceResourceLocation.fromNamespaceAndPath("masterworks", "wood");
        static final MaterialReferenceResourceLocation stone =
                MaterialReferenceResourceLocation.fromNamespaceAndPath("masterworks", "stone");
        static final MaterialReferenceResourceLocation iron =
                MaterialReferenceResourceLocation.fromNamespaceAndPath("masterworks", "iron");
        static final MaterialReferenceResourceLocation gold =
                MaterialReferenceResourceLocation.fromNamespaceAndPath("masterworks", "gold");
        static final MaterialReferenceResourceLocation diamond =
                MaterialReferenceResourceLocation.fromNamespaceAndPath("masterworks", "diamond");
        static final MaterialReferenceResourceLocation emerald =
                MaterialReferenceResourceLocation.fromNamespaceAndPath("masterworks", "emerald");

        static Construct simple(CompositionReferenceResourceLocation composition,
                MaterialReferenceResourceLocation material) {
            return new Construct(composition, Map.of(Construct.Component.Key.DEFAULT,
                    new Construct.Component(Either.left(material))));
        }

        static Construct composite(CompositionReferenceResourceLocation composition,
                Map<String, Construct> parts) {
            return new Construct(composition,
                    parts.entrySet().stream()
                            .map(entry -> Map.entry(new Construct.Component.Key(entry.getKey()),
                                    new Construct.Component(Either.right(entry.getValue()))))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        }

        @Override
        public void accept(@Nonnull CreativeModeTab.ItemDisplayParameters params,
                @Nonnull CreativeModeTab.Output output) {
            Construct woodRod = simple(rod, wood);
            Construct ironRod = simple(rod, iron);
            Construct diamondRod = simple(rod, diamond);

            Construct stoneBinding = simple(binding, stone);
            Construct ironBinding = simple(binding, iron);
            Construct emeraldBinding = simple(binding, emerald);

            Construct stonePickaxeHead = simple(pickaxeHead, stone);
            Construct goldPickaxeHead = simple(pickaxeHead, gold);

            Construct ironSwordBlade = simple(broadSwordBlade, iron);
            Construct diamondEdgeStraight = simple(broadSwordBladeStraightEdge, diamond);
            Construct emeraldEdgeStraight = simple(broadSwordBladeStraightEdge, emerald);
            Construct diamondEmeraldSwordBlade = composite(broadSwordBladeDual,
                    Map.of("left", diamondEdgeStraight, "right", emeraldEdgeStraight));

            output.accept(stack(stonePickaxeHead));
            output.accept(stack(ironRod));
            output.accept(stack(emeraldBinding));
            output.accept(stack(composite(pickaxe, Map.of("head", goldPickaxeHead, "handle",
                    ironRod, "binding", emeraldBinding))));

            output.accept(stack(ironSwordBlade));
            output.accept(stack(diamondRod));
            output.accept(stack(stoneBinding));
            output.accept(stack(composite(broadSword,
                    Map.of("blade", ironSwordBlade, "handle", diamondRod, "guard", stoneBinding))));

            output.accept(stack(diamondEdgeStraight));
            output.accept(stack(emeraldEdgeStraight));
            output.accept(stack(diamondEmeraldSwordBlade));
            output.accept(stack(woodRod));
            output.accept(stack(ironBinding));
            output.accept(stack(composite(broadSword, Map.of("blade", diamondEmeraldSwordBlade,
                    "handle", woodRod, "guard", ironBinding))));
        }
    }
}
