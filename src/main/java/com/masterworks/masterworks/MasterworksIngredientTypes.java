package com.masterworks.masterworks;

import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class MasterworksIngredientTypes {
    private static final DeferredRegister<IngredientType<?>> REGISTRAR =
            DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, MasterworksMod.ID);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    @SuppressWarnings("unused")
    private static <T extends ICustomIngredient> Supplier<IngredientType<T>> register(String name, MapCodec<T> codec) {
        return REGISTRAR.register(name, () -> new IngredientType<T>(codec));
    }
}
