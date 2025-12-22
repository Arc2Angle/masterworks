package com.masterworks.masterworks.client.render;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.masterworks.masterworks.MasterworksDataComponents;
import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.MasterworksPropertyTypes;
import com.masterworks.masterworks.client.draw.PixelUtils;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.core.RenderProperty;
import com.masterworks.masterworks.location.RoleReferenceLocation;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemStack;

public class ConstructSpecialModelRenderer extends NativeItemSpecialModelRenderer<Construct> {
    public record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<ConstructSpecialModelRenderer.Unbaked> MAP_CODEC =
                MapCodec.unit(ConstructSpecialModelRenderer.Unbaked::new);

        @Override
        public MapCodec<ConstructSpecialModelRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        @Nonnull
        public SpecialModelRenderer<?> bake(@Nonnull BakingContext context) {
            return new ConstructSpecialModelRenderer();
        }
    }

    @Override
    @Nullable
    public Construct extractArgument(@Nullable ItemStack stack) {
        if (stack == null) {
            return null;
        }

        return stack.get(MasterworksDataComponents.CONSTRUCT.get());
    }

    @Override
    @Nullable
    protected NativeImage getImage(@Nullable Construct argument) {
        if (argument == null) {
            return null;
        }

        try {
            RenderProperty property = argument.getPropertyOrThrow(
                    MasterworksPropertyTypes.RENDER.get(), RoleReferenceLocation.ITEM);

            return property.render(argument.components()).reduce(PixelUtils::Overlay).orElse(null);
        } catch (Construct.PropertyAccessException e) {
            MasterworksMod.LOGGER.warn(e.getMessage());
            return null;
        }
    }

}
