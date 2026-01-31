package com.masterworks.masterworks;

import com.masterworks.masterworks.recipe.ingredient.ItemConstructIngredient;
import com.masterworks.masterworks.recipe.ingredient.ItemMaterialIngredient;
import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class MasterworksIngredientTypes {
    static final DeferredRegister<IngredientType<?>> REGISTRAR =
            DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, MasterworksMod.ID);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    static <T extends ICustomIngredient> Supplier<IngredientType<T>> register(String name, MapCodec<T> codec) {
        return REGISTRAR.register(name, () -> new IngredientType<T>(codec));
    }

    public static final Supplier<IngredientType<ItemMaterialIngredient>> ITEM_MATERIAL =
            register("item_material", ItemMaterialIngredient.CODEC);

    public static final Supplier<IngredientType<ItemConstructIngredient>> ITEM_CONSTRUCT =
            register("item_construct", ItemConstructIngredient.CODEC);
}
