package com.masterworks.masterworks.item;

import com.masterworks.masterworks.MasterworksDataComponents;
import com.masterworks.masterworks.MasterworksDataPackRegistries;
import com.masterworks.masterworks.MasterworksItems;
import com.masterworks.masterworks.MasterworksRegistries;
import com.masterworks.masterworks.data.Composition;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.Material;
import com.mojang.datafixers.util.Either;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
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

        private static ResourceKey<Composition> composition(String path) {
            return ResourceKey.create(
                    MasterworksDataPackRegistries.COMPOSITION, Identifier.fromNamespaceAndPath("masterworks", path));
        }

        private static ResourceKey<Material> material(String path) {
            return ResourceKey.create(
                    MasterworksDataPackRegistries.MATERIAL, Identifier.fromNamespaceAndPath("masterworks", path));
        }

        private static final ResourceKey<Composition> rod = composition("rod");
        private static final ResourceKey<Composition> binding = composition("binding");
        private static final ResourceKey<Composition> pickaxe = composition("pickaxe");
        private static final ResourceKey<Composition> pickaxeHead = composition("pickaxe/head");
        private static final ResourceKey<Composition> swordBroad = composition("sword/broad");
        private static final ResourceKey<Composition> bladeBroad = composition("blade/broad");
        private static final ResourceKey<Composition> bladeBroadDual = composition("blade/broad/dual");
        private static final ResourceKey<Composition> edgeBroadStraight = composition("edge/broad/straight");
        private static final ResourceKey<Composition> edgeBroadSirrated = composition("edge/broad/sirrated");

        private static final ResourceKey<Material> wood = material("wood");
        private static final ResourceKey<Material> stone = material("stone");
        private static final ResourceKey<Material> iron = material("iron");
        private static final ResourceKey<Material> gold = material("gold");
        private static final ResourceKey<Material> diamond = material("diamond");
        private static final ResourceKey<Material> emerald = material("emerald");

        private static Construct simple(Holder<Composition> composition, Holder<Material> material) {
            return new Construct(
                    composition,
                    Map.of(new Construct.Component.Key("main"), new Construct.Component(Either.left(material))));
        }

        private static Construct complex(Holder<Composition> composition, Map<String, Construct> parts) {
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
            HolderLookup.RegistryLookup<Composition> compositions =
                    params.holders().lookupOrThrow(MasterworksDataPackRegistries.COMPOSITION);
            HolderLookup.RegistryLookup<Material> materials =
                    params.holders().lookupOrThrow(MasterworksDataPackRegistries.MATERIAL);

            Construct woodRod = simple(compositions.getOrThrow(rod), materials.getOrThrow(wood));
            Construct ironRod = simple(compositions.getOrThrow(rod), materials.getOrThrow(iron));
            Construct diamondRod = simple(compositions.getOrThrow(rod), materials.getOrThrow(diamond));

            Construct stoneBinding = simple(compositions.getOrThrow(binding), materials.getOrThrow(stone));
            Construct ironBinding = simple(compositions.getOrThrow(binding), materials.getOrThrow(iron));
            Construct emeraldBinding = simple(compositions.getOrThrow(binding), materials.getOrThrow(emerald));

            Construct stonePickaxeHead = simple(compositions.getOrThrow(pickaxeHead), materials.getOrThrow(stone));
            Construct goldPickaxeHead = simple(compositions.getOrThrow(pickaxeHead), materials.getOrThrow(gold));

            Construct ironBladeBroad = simple(compositions.getOrThrow(bladeBroad), materials.getOrThrow(iron));
            Construct diamondEdgeBroadSirrated =
                    simple(compositions.getOrThrow(edgeBroadSirrated), materials.getOrThrow(diamond));
            Construct emeraldEdgeBroadStraight =
                    simple(compositions.getOrThrow(edgeBroadStraight), materials.getOrThrow(emerald));
            Construct diamondEmeraldBladeBroad = complex(
                    compositions.getOrThrow(bladeBroadDual),
                    Map.of("left", diamondEdgeBroadSirrated, "right", emeraldEdgeBroadStraight));

            output.accept(stack(stonePickaxeHead));
            output.accept(stack(ironRod));
            output.accept(stack(emeraldBinding));
            output.accept(stack(complex(
                    compositions.getOrThrow(pickaxe),
                    Map.of("head", goldPickaxeHead, "handle", ironRod, "binding", emeraldBinding))));

            output.accept(stack(ironBladeBroad));
            output.accept(stack(diamondRod));
            output.accept(stack(stoneBinding));
            output.accept(stack(complex(
                    compositions.getOrThrow(swordBroad),
                    Map.of("blade", ironBladeBroad, "handle", diamondRod, "guard", stoneBinding))));

            output.accept(stack(diamondEdgeBroadSirrated));
            output.accept(stack(emeraldEdgeBroadStraight));
            output.accept(stack(diamondEmeraldBladeBroad));
            output.accept(stack(woodRod));
            output.accept(stack(ironBinding));
            output.accept(stack(complex(
                    compositions.getOrThrow(swordBroad),
                    Map.of("blade", diamondEmeraldBladeBroad, "handle", woodRod, "guard", ironBinding))));
        }
    }
}
