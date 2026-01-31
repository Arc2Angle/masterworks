package com.masterworks.masterworks.recipe.ingredient;

import com.masterworks.masterworks.MasterworksDataMapTypes;
import com.masterworks.masterworks.MasterworksDataPackRegistries;
import com.masterworks.masterworks.MasterworksIngredientTypes;
import com.masterworks.masterworks.data.Material;
import com.masterworks.masterworks.location.MaterialReferenceLocation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

public record ItemMaterialIngredient(Optional<TagKey<Material>> materialTag) implements ICustomIngredient {

    public static final MapCodec<ItemMaterialIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.optionalField("material_tag", TagKey.codec(MasterworksDataPackRegistries.MATERIAL), false)
                            .forGetter(i -> i.materialTag))
            .apply(instance, ItemMaterialIngredient::new));

    @Override
    public IngredientType<?> getType() {
        return MasterworksIngredientTypes.ITEM_MATERIAL.get();
    }

    @Override
    public boolean isSimple() {
        return true;
    }

    @Override
    public Stream<Holder<Item>> items() {
        return BuiltInRegistries.ITEM.stream()
                .map(BuiltInRegistries.ITEM::wrapAsHolder)
                .filter(holder -> testMaterialReference(holder.getData(MasterworksDataMapTypes.ITEM_MATERIAL)));
    }

    boolean testMaterialReference(@Nullable MaterialReferenceLocation material) {
        if (material == null) {
            return false;
        }
        if (materialTag.isEmpty()) {
            return true;
        }
        TagKey<Material> ingredientTag = materialTag.orElseThrow();

        return material.registered().tags().anyMatch(tag -> ingredientTag.equals(tag));
    }

    @Override
    public boolean test(@Nonnull ItemStack stack) {
        return testMaterialReference(stack.getItemHolder().getData(MasterworksDataMapTypes.ITEM_MATERIAL));
    }
}
