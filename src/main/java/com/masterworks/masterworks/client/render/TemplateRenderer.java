package com.masterworks.masterworks.client.render;

import java.io.Closeable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.masterworks.masterworks.resource.location.ShapeResourceLocation;
import com.masterworks.masterworks.resource.location.TierResourceLocation;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class TemplateRenderer extends NativeItemSpecialRenderer<Void> implements Closeable {
    private final NativeImage image;

    private TemplateRenderer(NativeImage image) {
        this.image = image;
    }

    @Override
    public void close() {
        image.close();
    }

    @Override
    @Nullable
    public Void extractArgument(@Nullable ItemStack stack) {
        return null;
    }

    @Override
    @Nullable
    protected NativeImage getImage(@Nullable Void argument) {
        return image;
    }

    public record Unbaked(ShapeResourceLocation shape, TierResourceLocation tier)
            implements SpecialModelRenderer.Unbaked {

        public static final MapCodec<TemplateRenderer.Unbaked> MAP_CODEC =
                RecordCodecBuilder.mapCodec(instance -> instance
                        .group(ShapeResourceLocation.CODEC.fieldOf("shape")
                                .forGetter(Unbaked::shape),
                                TierResourceLocation.CODEC.fieldOf("tier").forGetter(Unbaked::tier))
                        .apply(instance, Unbaked::new));

        @Override
        public MapCodec<TemplateRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(@Nonnull EntityModelSet modelSet) {
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

            try (NativeImage backboard = tier.getTexture(resourceManager);
                    NativeImage silhouette = shape.getTexture(resourceManager)) {

                TemplateRenderer renderer = new TemplateRenderer(etch(backboard, silhouette));
                Runtime.getRuntime().addShutdownHook(new Thread(renderer::close));

                return renderer;
            } catch (Exception e) {
                throw new RuntimeException("Failed to load template components", e);
            }
        }
    }

    private static NativeImage etch(NativeImage backboard, NativeImage silhouette) {
        int width = backboard.getWidth();
        int height = backboard.getHeight();

        NativeImage result = new NativeImage(width, height, true);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int backboardColor = backboard.getPixel(x, y);

                if (ARGB.alpha(backboardColor) == 0) {
                    continue;
                }

                int silhouetteColor = silhouette.getPixel(x, y);
                int factor = ARGB.alpha(silhouetteColor) == 0 ? 0xFF : 0x7F;
                result.setPixel(x, y, ARGB.scaleRGB(backboardColor, factor));
            }
        }

        return result;
    }
}
