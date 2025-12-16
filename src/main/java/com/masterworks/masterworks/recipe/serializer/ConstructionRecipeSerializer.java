package com.masterworks.masterworks.recipe.serializer;

import com.masterworks.masterworks.recipe.ConstructionRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ConstructionRecipeSerializer implements RecipeSerializer<ConstructionRecipe> {

    public static final MapCodec<ConstructionRecipe> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Ingredient.CODEC.fieldOf("template").forGetter(ConstructionRecipe::template),
                    Codec.list(Ingredient.CODEC).fieldOf("components")
                            .forGetter(ConstructionRecipe::components))
                    .apply(instance, ConstructionRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ConstructionRecipe> STREAM_CODEC =
            StreamCodec.composite(Ingredient.CONTENTS_STREAM_CODEC, ConstructionRecipe::template,
                    Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
                    ConstructionRecipe::components, ConstructionRecipe::new);

    @Override
    public MapCodec<ConstructionRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ConstructionRecipe> streamCodec() {
        return STREAM_CODEC;
    }

}
