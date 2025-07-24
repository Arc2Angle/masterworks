package com.masterworks.masterworks.client.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import com.masterworks.masterworks.component.DataComponents;
import com.masterworks.masterworks.Masterworks;

/**
 * SelectItemModelProperty that determines which model to use based on the tool part type. This
 * reads the part type from the ItemStack's DataComponents and returns the appropriate
 * ResourceLocation for model selection (blade_template, handle_template, binding_template).
 */
public class PartTypeSelectProperty implements SelectItemModelProperty<ResourceLocation> {
    public static final Type<PartTypeSelectProperty, ResourceLocation> TYPE =
            SelectItemModelProperty.Type.create(
                    // empty map codec
                    MapCodec.unit(new PartTypeSelectProperty()), ResourceLocation.CODEC);

    private static final ResourceLocation DEFAULT_PART_TYPE =
            ResourceLocation.fromNamespaceAndPath(Masterworks.MOD_ID, "default");

    @Override
    @Nullable
    public ResourceLocation get(@Nonnull ItemStack stack, @Nullable ClientLevel level,
            @Nullable LivingEntity entity, int seed, @Nonnull ItemDisplayContext displayContext) {
        ResourceLocation partTypeId = stack.get(DataComponents.PART_TYPE_ITEM.get());

        if (partTypeId == null) {
            return DEFAULT_PART_TYPE;
        }

        return partTypeId;
    }

    @Override
    public Type<PartTypeSelectProperty, ResourceLocation> type() {
        return TYPE;
    }

    @Override
    public Codec<ResourceLocation> valueCodec() {
        return ResourceLocation.CODEC;
    }
}
