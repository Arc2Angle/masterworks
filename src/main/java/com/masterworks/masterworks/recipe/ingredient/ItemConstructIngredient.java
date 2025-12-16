package com.masterworks.masterworks.recipe.ingredient;

import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import com.masterworks.masterworks.init.MasterworksDataComponents;
import com.masterworks.masterworks.init.MasterworksIngredientTypes;
import com.masterworks.masterworks.resource.location.CompositionReferenceResourceLocation;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

public record ItemConstructIngredient(CompositionReferenceResourceLocation composition)
        implements ICustomIngredient {

    public static final MapCodec<ItemConstructIngredient> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance
                    .group(CompositionReferenceResourceLocation.CODEC.fieldOf("composition")
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
