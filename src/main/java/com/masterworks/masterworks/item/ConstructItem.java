package com.masterworks.masterworks.item;

import com.masterworks.masterworks.MasterworksDataComponents;
import com.masterworks.masterworks.MasterworksItems;
import com.masterworks.masterworks.MasterworksRegistries;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.location.CompositionReferenceLocation;
import com.masterworks.masterworks.location.MaterialReferenceLocation;
import com.mojang.datafixers.util.Either;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ConstructItem extends Item {
    public ConstructItem(Item.Properties properties) {
        super(properties);
    }

    public static ItemStack stack(Construct construct) {
        ItemStack stack = new ItemStack(MasterworksItems.CONSTRUCT.get());

        stack.set(MasterworksDataComponents.CONSTRUCT.get(), construct);

        MasterworksRegistries.PROPERTY_APPLIER.entrySet().forEach(entry -> entry.getValue()
                .apply(construct, stack));

        return stack;
    }

    public static class DisplayItemsGenerator implements CreativeModeTab.DisplayItemsGenerator {
        static final CompositionReferenceLocation rod =
                CompositionReferenceLocation.fromNamespaceAndPath("masterworks", "rod");
        static final CompositionReferenceLocation binding =
                CompositionReferenceLocation.fromNamespaceAndPath("masterworks", "binding");
        static final CompositionReferenceLocation pickaxe =
                CompositionReferenceLocation.fromNamespaceAndPath("masterworks", "pickaxe");
        static final CompositionReferenceLocation pickaxeHead =
                CompositionReferenceLocation.fromNamespaceAndPath("masterworks", "pickaxe/head");
        static final CompositionReferenceLocation broadSword =
                CompositionReferenceLocation.fromNamespaceAndPath("masterworks", "sword/broad");
        static final CompositionReferenceLocation broadSwordBlade =
                CompositionReferenceLocation.fromNamespaceAndPath("masterworks", "sword/broad/blade");
        static final CompositionReferenceLocation broadSwordBladeDual =
                CompositionReferenceLocation.fromNamespaceAndPath("masterworks", "sword/broad/blade/dual");
        static final CompositionReferenceLocation broadSwordBladeStraightEdge =
                CompositionReferenceLocation.fromNamespaceAndPath("masterworks", "sword/broad/blade/edge/straight");

        static final MaterialReferenceLocation wood =
                MaterialReferenceLocation.fromNamespaceAndPath("masterworks", "wood");
        static final MaterialReferenceLocation stone =
                MaterialReferenceLocation.fromNamespaceAndPath("masterworks", "stone");
        static final MaterialReferenceLocation iron =
                MaterialReferenceLocation.fromNamespaceAndPath("masterworks", "iron");
        static final MaterialReferenceLocation gold =
                MaterialReferenceLocation.fromNamespaceAndPath("masterworks", "gold");
        static final MaterialReferenceLocation diamond =
                MaterialReferenceLocation.fromNamespaceAndPath("masterworks", "diamond");
        static final MaterialReferenceLocation emerald =
                MaterialReferenceLocation.fromNamespaceAndPath("masterworks", "emerald");

        static Construct simple(CompositionReferenceLocation composition, MaterialReferenceLocation material) {
            return new Construct(
                    composition,
                    Map.of(Construct.Component.Key.DEFAULT, new Construct.Component(Either.left(material))));
        }

        static Construct composite(CompositionReferenceLocation composition, Map<String, Construct> parts) {
            return new Construct(
                    composition,
                    parts.entrySet().stream()
                            .map(entry -> Map.entry(
                                    new Construct.Component.Key(entry.getKey()),
                                    new Construct.Component(Either.right(entry.getValue()))))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        }

        @Override
        public void accept(
                @Nonnull CreativeModeTab.ItemDisplayParameters params, @Nonnull CreativeModeTab.Output output) {
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
            Construct diamondEmeraldSwordBlade =
                    composite(broadSwordBladeDual, Map.of("left", diamondEdgeStraight, "right", emeraldEdgeStraight));

            output.accept(stack(stonePickaxeHead));
            output.accept(stack(ironRod));
            output.accept(stack(emeraldBinding));
            output.accept(stack(
                    composite(pickaxe, Map.of("head", goldPickaxeHead, "handle", ironRod, "binding", emeraldBinding))));

            output.accept(stack(ironSwordBlade));
            output.accept(stack(diamondRod));
            output.accept(stack(stoneBinding));
            output.accept(stack(composite(
                    broadSword, Map.of("blade", ironSwordBlade, "handle", diamondRod, "guard", stoneBinding))));

            output.accept(stack(diamondEdgeStraight));
            output.accept(stack(emeraldEdgeStraight));
            output.accept(stack(diamondEmeraldSwordBlade));
            output.accept(stack(woodRod));
            output.accept(stack(ironBinding));
            output.accept(stack(composite(
                    broadSword, Map.of("blade", diamondEmeraldSwordBlade, "handle", woodRod, "guard", ironBinding))));
        }
    }
}
