package com.masterworks.masterworks.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import javax.annotation.Nonnull;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.init.MasterworksDataComponents;
import com.masterworks.masterworks.init.MasterworksItems;
import com.masterworks.masterworks.init.MasterworksRegistries;
import com.masterworks.masterworks.resource.location.MaterialReferenceResourceLocation;
import com.masterworks.masterworks.resource.location.CompositionReferenceResourceLocation;
import com.mojang.datafixers.util.Either;
import java.util.Map;
import java.util.stream.Collectors;

public class ConstructItem extends Item {
    public ConstructItem(Item.Properties properties) {
        super(properties);
    }

    public static ItemStack stack(Construct construct) {
        ItemStack stack = new ItemStack(MasterworksItems.CONSTRUCT.get());

        stack.set(MasterworksDataComponents.CONSTRUCT.get(), construct);

        MasterworksRegistries.PROPERTY_APPLIER.entrySet().forEach(entry -> {
            Property.Applier applier = entry.getValue();
            applier.apply(construct, stack);
        });

        return stack;
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
