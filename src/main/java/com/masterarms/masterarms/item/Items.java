package com.masterarms.masterarms.item;

import com.masterarms.masterarms.Masterarms;
import com.masterarms.masterarms.material.Material;
import com.masterarms.masterarms.material.Materials;
import com.masterarms.masterarms.part.type.PartType;
import com.masterarms.masterarms.part.type.PartTypes;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class Items {

        public static final DeferredHolder<Item, PartItem> PART =
                        Masterarms.ITEMS.register("part", () -> new PartItem());

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
