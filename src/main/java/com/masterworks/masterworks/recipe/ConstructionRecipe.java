package com.masterworks.masterworks.recipe;

import com.masterworks.masterworks.MasterworksRecipeSerializers;
import com.masterworks.masterworks.MasterworksRecipeTypes;
import com.masterworks.masterworks.recipe.input.ConstructionRecipeInput;
import com.masterworks.masterworks.util.stream.BiStream;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record ConstructionRecipe(Ingredient template, List<Ingredient> components)
        implements Recipe<ConstructionRecipeInput> {

    @Override
    public RecipeType<? extends Recipe<ConstructionRecipeInput>> getType() {
        return MasterworksRecipeTypes.CONSTRUCTION.get();
    }

    @Override
    public boolean matches(@Nonnull ConstructionRecipeInput input, @Nonnull Level level) {
        return template.test(input.template())
                && BiStream.zip(components.stream(), input.components().stream())
                        .map(Ingredient::test)
                        .allMatch(b -> b);
    }

    @Override
    public ItemStack assemble(@Nonnull ConstructionRecipeInput input, @Nonnull HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.create(
                Stream.concat(Stream.of(template), components.stream()).toList());
    }

    @Override
    public RecipeSerializer<? extends Recipe<ConstructionRecipeInput>> getSerializer() {
        return MasterworksRecipeSerializers.CONSTRUCTION.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        throw new UnsupportedOperationException("Construction Recipe does not appear in the recipe book");
    }
}
