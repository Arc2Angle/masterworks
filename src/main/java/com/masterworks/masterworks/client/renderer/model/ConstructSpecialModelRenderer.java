package com.masterworks.masterworks.client.renderer.model;

import com.masterworks.masterworks.MasterworksDataComponents;
import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.MasterworksPropertyTypes;
import com.masterworks.masterworks.client.draw.PixelUtils;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.core.RenderItemProperty;
import com.masterworks.masterworks.location.RoleReferenceLocation;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemStack;

public class ConstructSpecialModelRenderer extends NativeImageSpecialModelRenderer<Construct> {
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

        RenderItemProperty property = argument.properties(RoleReferenceLocation.ITEM)
                .get(MasterworksPropertyTypes.RENDER_ITEM.get())
                .orElse(null);

        if (property == null) {
            MasterworksMod.LOGGER.warn("Missing render property for construct " + argument);
            return null;
        }

        return property.render(argument.components())
                .reduce(PixelUtils::Overlay)
                .orElse(null);
    }
}
