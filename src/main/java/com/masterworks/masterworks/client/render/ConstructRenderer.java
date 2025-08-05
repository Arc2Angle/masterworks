package com.masterworks.masterworks.client.render;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.masterworks.masterworks.client.generate.ConstructPixelsManager;
import com.masterworks.masterworks.data.construct.Construct;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemStack;

public class ConstructRenderer extends NativeItemSpecialRenderer<Construct> {

    public record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<ConstructRenderer.Unbaked> MAP_CODEC =
                MapCodec.unit(ConstructRenderer.Unbaked::new);

        @Override
        public MapCodec<ConstructRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        @Nonnull
        public SpecialModelRenderer<?> bake(@Nonnull EntityModelSet modelSet) {
            return new ConstructRenderer();
        }
    }

    @Override
    @Nullable
    public Construct extractArgument(@Nullable ItemStack stack) {
        if (stack == null) {
            return null;
        }

        return stack.get(Construct.DATA_COMPONENT.get());
    }

    @Override
    @Nullable
    protected NativeImage getImage(@Nullable Construct argument) {
        if (argument == null) {
            return null;
        }

        return ConstructPixelsManager.getInstance().get(argument);
    }
}
