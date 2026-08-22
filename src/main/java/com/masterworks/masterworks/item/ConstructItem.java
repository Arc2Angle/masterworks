package com.masterworks.masterworks.item;

import com.masterworks.masterworks.MasterworksDataComponents;
import com.masterworks.masterworks.MasterworksDataPackRegistries;
import com.masterworks.masterworks.MasterworksItems;
import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.MasterworksRegistries;
import com.masterworks.masterworks.client.renderlet.MaterialRenderlet;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.Material;
import com.masterworks.masterworks.data.composition.Composition;
import com.mojang.datafixers.util.Either;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
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

    private record ResourceResolver<T>(
            String namespace,
            HolderLookup.RegistryLookup<T> registryLookup,
            ResourceKey<? extends Registry<T>> registryKey) {
        public static <T> ResourceResolver<T> of(
                HolderLookup.Provider lookupProvider, ResourceKey<? extends Registry<T>> registryKey) {
            return new ResourceResolver<>(MasterworksMod.ID, lookupProvider.lookupOrThrow(registryKey), registryKey);
        }

        public Holder<T> resolve(String path) {
            Identifier id = Identifier.fromNamespaceAndPath(namespace, path);
            ResourceKey<T> key = ResourceKey.create(registryKey, id);
            return registryLookup.getOrThrow(key);
        }
    }

    public static class DisplayItemsGenerator implements CreativeModeTab.DisplayItemsGenerator {

        private static Construct construct(Holder<Composition> composition, Holder<Material> material) {
            return new Construct(
                    composition,
                    Map.of(MaterialRenderlet.COMPONENT_KEY, new Construct.Component(Either.left(material))));
        }

        private static Construct construct(Holder<Composition> composition, Map<String, Construct> components) {
            return new Construct(
                    composition,
                    components.entrySet().stream()
                            .collect(Collectors.toMap(
                                    entry -> new Construct.Component.Key(entry.getKey()),
                                    entry -> new Construct.Component(Either.right(entry.getValue())))));
        }

        @Override
        public void accept(
                @Nonnull CreativeModeTab.ItemDisplayParameters params, @Nonnull CreativeModeTab.Output output) {
            final ResourceResolver<Material> materials =
                    ResourceResolver.of(params.holders(), MasterworksDataPackRegistries.MATERIAL);
            final Holder<Material>
                    // vanilla materials
                    wood = materials.resolve("wood"),
                    stone = materials.resolve("stone"),
                    iron = materials.resolve("iron"),
                    gold = materials.resolve("gold"),
                    diamond = materials.resolve("diamond"),
                    emerald = materials.resolve("emerald");

            final ResourceResolver<Composition> compositions =
                    ResourceResolver.of(params.holders(), MasterworksDataPackRegistries.COMPOSITION);
            final Holder<Composition>
                    // (generic)
                    rod = compositions.resolve("rod"),
                    binding = compositions.resolve("binding"),

                    // pickaxe/head
                    pickaxeHead = compositions.resolve("pickaxe_head"),

                    // sword/blade
                    blade = compositions.resolve("blade"),
                    bladeDual = compositions.resolve("blade/dual"),

                    // blade/left_edge, blade/right_edge, sword/blade/left_edge, sword/blade/right_edge
                    edgeStraight = compositions.resolve("edge/straight"),
                    edgeSerrated = compositions.resolve("edge/serrated"),

                    // Possible refactor: remove edge/, have blade/broad (concrete), blade/broad/dual (abstract),
                    // blade/serrated (concrete), blade/straight (concrete)

                    // elastic_webbing (head)
                    headTightcover = compositions.resolve("tightcover/head"),

                    // elastic_webbing (chest)
                    chestTightcover = compositions.resolve("tightcover/chest"),

                    // rigid_shell (head, chest, legs, feet)
                    plate = compositions.resolve("plate"),
                    chainmail = compositions.resolve("chainmail"),

                    // malleable_lining (head, chest, legs, feet)
                    fulleatherLining = compositions.resolve("lining/fulleather"),
                    foamLining = compositions.resolve("lining/foam"),

                    // item/layer
                    pickaxe = compositions.resolve("pickaxe"),
                    sword = compositions.resolve("sword"),
                    hardhat = compositions.resolve("hardhat"),
                    torsovest = compositions.resolve("torsovest");

            Construct woodRod = construct(rod, wood);
            Construct ironRod = construct(rod, iron);
            Construct diamondRod = construct(rod, diamond);
            output.accept(stack(woodRod));
            output.accept(stack(ironRod));
            output.accept(stack(diamondRod));

            Construct stoneBinding = construct(binding, stone);
            Construct ironBinding = construct(binding, iron);
            Construct emeraldBinding = construct(binding, emerald);
            output.accept(stack(stoneBinding));
            output.accept(stack(ironBinding));
            output.accept(stack(emeraldBinding));

            Construct woodHeadTightcover = construct(headTightcover, wood);
            output.accept(stack(woodHeadTightcover));

            Construct woodPlate = construct(plate, wood);
            Construct stonePlate = construct(plate, iron);
            Construct diamondPlate = construct(plate, diamond);
            output.accept(stack(woodPlate));
            output.accept(stack(stonePlate));
            output.accept(stack(diamondPlate));

            Construct woodFulleatherLining = construct(fulleatherLining, wood);
            Construct goldFulleatherLining = construct(fulleatherLining, gold);
            output.accept(stack(woodFulleatherLining));
            output.accept(stack(goldFulleatherLining));

            Construct stonePickaxeHead = construct(pickaxeHead, stone);
            Construct goldPickaxeHead = construct(pickaxeHead, gold);
            output.accept(stack(stonePickaxeHead));
            output.accept(stack(goldPickaxeHead));

            Construct ironBlade = construct(blade, iron);
            output.accept(stack(ironBlade));

            Construct diamondEdgeSerrated = construct(edgeSerrated, diamond);
            Construct emeraldEdgeStraight = construct(edgeStraight, emerald);
            Construct diamondEmeraldBlade =
                    construct(bladeDual, Map.of("left", diamondEdgeSerrated, "right", emeraldEdgeStraight));
            output.accept(stack(diamondEdgeSerrated));
            output.accept(stack(emeraldEdgeStraight));
            output.accept(stack(diamondEmeraldBlade));

            output.accept(stack(
                    construct(pickaxe, Map.of("head", goldPickaxeHead, "handle", ironRod, "binding", emeraldBinding))));
            output.accept(
                    stack(construct(sword, Map.of("blade", ironBlade, "handle", diamondRod, "guard", stoneBinding))));
            output.accept(stack(
                    construct(sword, Map.of("blade", diamondEmeraldBlade, "handle", woodRod, "guard", ironBinding))));
            output.accept(stack(construct(
                    hardhat,
                    Map.of("webbing", woodHeadTightcover, "shell", diamondPlate, "lining", goldFulleatherLining))));
        }
    }
}
