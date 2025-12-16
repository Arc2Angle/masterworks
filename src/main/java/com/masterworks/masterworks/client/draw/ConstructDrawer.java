package com.masterworks.masterworks.client.draw;

import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.resource.location.PaletteReferenceResourceLocation;
import com.masterworks.masterworks.resource.location.ShapeReferenceResourceLocation;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;

public class ConstructDrawer extends CachedDrawer<ConstructDrawer.Params> {

    public static final PaletteReferenceResourceLocation PALETTE_DEFAULT =
            PaletteReferenceResourceLocation.fromNamespaceAndPath(MasterworksMod.ID, "none");

    public static final ShapeReferenceResourceLocation SHAPE_DEFAULT =
            ShapeReferenceResourceLocation.fromNamespaceAndPath(MasterworksMod.ID, "orb");

    private ConstructDrawer() {
        preloadTexture(PALETTE_DEFAULT);
        preloadTexture(SHAPE_DEFAULT);
        onResourceManagerReload(Minecraft.getInstance().getResourceManager());
    }

    private static ConstructDrawer instance;

    public static ConstructDrawer instance() {
        if (instance == null) {
            instance = new ConstructDrawer();
            Runtime.getRuntime().addShutdownHook(new Thread(instance::close));
        }
        return instance;
    }

    public NativeImage get(PaletteReferenceResourceLocation palette,
            ShapeReferenceResourceLocation shape) {
        return super.get(new Params(palette, shape));
    }

    @Override
    protected NativeImage generate(Params params) {
        MasterworksMod.LOGGER.info("Drawing model with palette {} and shape {}", params.palette(),
                params.shape());

        try (NativeImage palette =
                loadTextureWithPreloadedDefault(params.palette(), PALETTE_DEFAULT);
                NativeImage shape =
                        loadTextureWithPreloadedDefault(params.shape(), SHAPE_DEFAULT)) {

            return shape.mappedCopy(new PixelUtils.GrayscaleColorer(palette));
        }
    }

    protected record Params(PaletteReferenceResourceLocation palette,
            ShapeReferenceResourceLocation shape) {
    }
}
