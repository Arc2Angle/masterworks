package com.masterworks.masterworks.recipe.input;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record ConstructionRecipeInput(ItemStack template, NonNullList<ItemStack> components)
        implements RecipeInput {

    @Override
    public ItemStack getItem(int slot) {
        if (slot == 0) {
            return template;
        }
        return components.get(slot - 1);
    }

    @Override
    public int size() {
        return components.size() + 1;
    }
}
