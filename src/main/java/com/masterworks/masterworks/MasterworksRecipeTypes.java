package com.masterworks.masterworks;

import java.util.function.Function;
import java.util.function.Supplier;
import com.masterworks.masterworks.recipe.ConstructionRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MasterworksRecipeTypes {
    static final DeferredRegister<RecipeType<?>> REGISTRAR =
            DeferredRegister.create(Registries.RECIPE_TYPE, MasterworksMod.ID);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    static <T extends Recipe<?>> Supplier<RecipeType<T>> register(String name,
            Function<ResourceLocation, ? extends RecipeType<T>> factory) {
        return REGISTRAR.register(name, factory);
    }



    public static final Supplier<RecipeType<ConstructionRecipe>> CONSTRUCTION =
            register("construction", RecipeType::simple);
}
