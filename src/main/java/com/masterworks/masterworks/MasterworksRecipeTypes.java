package com.masterworks.masterworks;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MasterworksRecipeTypes {
    private static final DeferredRegister<RecipeType<?>> REGISTRAR =
            DeferredRegister.create(Registries.RECIPE_TYPE, MasterworksMod.ID);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    @SuppressWarnings("unused")
    private static <T extends Recipe<?>> Supplier<RecipeType<T>> register(
            String name, Function<Identifier, ? extends RecipeType<T>> factory) {
        return REGISTRAR.register(name, factory);
    }
}
