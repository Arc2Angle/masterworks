package com.masterworks.masterworks.client.render;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.masterworks.masterworks.client.draw.TemplateDrawer;
import com.masterworks.masterworks.data.Template;
import com.masterworks.masterworks.init.MasterworksDataComponents;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemStack;

public class TemplateSpecialModelRenderer extends NativeItemSpecialModelRenderer<Template> {
    public record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<TemplateSpecialModelRenderer.Unbaked> MAP_CODEC =
                MapCodec.unit(TemplateSpecialModelRenderer.Unbaked::new);

        @Override
        public MapCodec<TemplateSpecialModelRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        @Nonnull
        public SpecialModelRenderer<?> bake(@Nonnull BakingContext context) {
            return new TemplateSpecialModelRenderer();
        }
    }

    @Override
    @Nullable
    public Template extractArgument(@Nullable ItemStack stack) {
        if (stack == null) {
            return null;
        }

        return stack.get(MasterworksDataComponents.TEMPLATE.get());
    }

    @Override
    @Nullable
    protected NativeImage getImage(@Nullable Template argument) {
        if (argument == null) {
            return null;
        }

        return TemplateDrawer.instance().get(argument.tier(), argument.shape());
    }
}
