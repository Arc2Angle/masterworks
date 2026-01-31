package com.masterworks.masterworks.client.draw;

import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.location.PaletteReferenceLocation;
import com.masterworks.masterworks.location.ShapeReferenceLocation;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;

public class ConstructDrawer extends CachedDrawer<ConstructDrawer.Params> {

    public static final PaletteReferenceLocation PALETTE_DEFAULT =
            PaletteReferenceLocation.fromNamespaceAndPath(MasterworksMod.ID, "none");

    public static final ShapeReferenceLocation SHAPE_DEFAULT =
            ShapeReferenceLocation.fromNamespaceAndPath(MasterworksMod.ID, "orb");

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

    public NativeImage get(PaletteReferenceLocation palette, ShapeReferenceLocation shape) {
        return super.get(new Params(palette, shape));
    }

    @Override
    protected NativeImage generate(Params params) {
        MasterworksMod.LOGGER.info("Drawing model with palette {} and shape {}", params.palette(), params.shape());

        try (NativeImage palette = loadTextureWithPreloadedDefault(params.palette(), PALETTE_DEFAULT);
                NativeImage shape = loadTextureWithPreloadedDefault(params.shape(), SHAPE_DEFAULT)) {

            return shape.mappedCopy(new PixelUtils.GrayscaleColorer(palette));
        }
    }

    protected record Params(PaletteReferenceLocation palette, ShapeReferenceLocation shape) {}
}
