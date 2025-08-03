package com.masterworks.masterworks.client.render;

import javax.annotation.Nonnull;
import com.masterworks.masterworks.client.render.direct.NativeItemSpecialRenderer;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.ResourceLocation;

public class TemplateRenderer extends NativeItemSpecialRenderer<Void> {
    private final NativeImage image;

    public TemplateRenderer(NativeImage image) {
        this.image = image;
    }

    public record Unbaked(ResourceLocation shape) implements SpecialModelRenderer.Unbaked {

        public static final MapCodec<TemplateRenderer.Unbaked> MAP_CODEC =
                ResourceLocation.CODEC.fieldOf("shape").xmap(TemplateRenderer.Unbaked::new,
                        TemplateRenderer.Unbaked::shape);

        @Override
        public MapCodec<TemplateRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(@Nonnull EntityModelSet modelSet) {
            ResourceLocation texture = shape.withPrefix("textures/construct/").withSuffix(".png");

            // Get the model and the material to render
            return new TemplateRenderer(texture);
        }
    }

}
