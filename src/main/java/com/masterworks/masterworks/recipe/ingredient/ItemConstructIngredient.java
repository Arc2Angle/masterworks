package com.masterworks.masterworks.recipe.ingredient;

import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import com.masterworks.masterworks.MasterworksDataComponents;
import com.masterworks.masterworks.MasterworksIngredientTypes;
import com.masterworks.masterworks.location.CompositionReferenceLocation;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

public record ItemConstructIngredient(CompositionReferenceLocation composition)
        implements ICustomIngredient {

    public static final MapCodec<ItemConstructIngredient> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance
                    .group(CompositionReferenceLocation.CODEC.fieldOf("composition")
                            .forGetter(ItemConstructIngredient::composition))
                    .apply(instance, ItemConstructIngredient::new));

    @Override
    public IngredientType<?> getType() {
        return MasterworksIngredientTypes.ITEM_CONSTRUCT.get();
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public Stream<Holder<Item>> items() {
        return Stream.empty();
    }

    @Override
    public boolean test(@Nonnull ItemStack stack) {
        return Optional.ofNullable(stack.get(MasterworksDataComponents.CONSTRUCT))
                .map(construct -> construct.composition().equals(composition)).orElse(false);
    }

}
