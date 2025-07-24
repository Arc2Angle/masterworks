package com.masterworks.masterworks.item;

import net.neoforged.neoforge.registries.DeferredHolder;
import com.masterworks.masterworks.Masterworks;
import com.masterworks.masterworks.material.Material;
import com.masterworks.masterworks.material.Materials;
import com.masterworks.masterworks.part.type.PartType;
import com.masterworks.masterworks.part.type.PartTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class Items {

        public static void init() {}

        public static final DeferredHolder<Item, PartItem> PART =
                        Masterworks.ITEMS.registerItem("part", PartItem::new);

        public static void addAllParts(java.util.function.Consumer<ItemStack> output) {
                Material[] materials =
                                new Material[] {Materials.IRON.get(), Materials.DIAMOND.get(),
                                                Materials.NETHERITE.get(), Materials.MYTHRIL.get()};

                PartType[] partTypes = new PartType[] {PartTypes.BLADE.get(),
                                PartTypes.HANDLE.get(), PartTypes.BINDING.get()};

                for (Material material : materials) {
                        for (PartType partType : partTypes) {
                                output.accept(PartItem.create(PART.get(), material, partType));
                        }
                }
        }
}
