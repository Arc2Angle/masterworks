package com.masterworks.masterworks.init;

import java.util.function.Supplier;
import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.recipe.ConstructionRecipe;
import com.masterworks.masterworks.recipe.serializer.ConstructionRecipeSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MasterworksRecipeSerializers {
    static final DeferredRegister<RecipeSerializer<?>> REGISTRAR =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, MasterworksMod.ID);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    static <T extends Recipe<?>> Supplier<RecipeSerializer<T>> register(String name,
            Supplier<? extends RecipeSerializer<T>> factory) {
        return REGISTRAR.register(name, factory);
    }



    public static final Supplier<RecipeSerializer<ConstructionRecipe>> CONSTRUCTION =
            register("construction", ConstructionRecipeSerializer::new);
}
