package com.masterworks.masterworks.client.draw;

import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.resource.location.ShapeReferenceResourceLocation;
import com.masterworks.masterworks.resource.location.TierReferenceResourceLocation;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;

public class TemplateDrawer extends CachedDrawer<TemplateDrawer.Params> {

    public static final TierReferenceResourceLocation TIER_DEFAULT =
            TierReferenceResourceLocation.fromNamespaceAndPath(MasterworksMod.ID, "basic");

    public static final ShapeReferenceResourceLocation SHAPE_DEFAULT =
            ShapeReferenceResourceLocation.fromNamespaceAndPath(MasterworksMod.ID, "orb");

    private TemplateDrawer() {
        preloadTexture(TIER_DEFAULT);
        preloadTexture(SHAPE_DEFAULT);
        onResourceManagerReload(Minecraft.getInstance().getResourceManager());
    }

    private static TemplateDrawer instance;

    public static TemplateDrawer instance() {
        if (instance == null) {
            instance = new TemplateDrawer();
            Runtime.getRuntime().addShutdownHook(new Thread(instance::close));
        }
        return instance;
    }

    public NativeImage get(TierReferenceResourceLocation tier,
            ShapeReferenceResourceLocation shape) {
        return super.get(new Params(tier, shape));
    }

    @Override
    protected NativeImage generate(Params params) {
        MasterworksMod.LOGGER.info("Drawing template with tier {} and shape {}", params.tier(),
                params.shape());

        try (NativeImage tier = loadTextureWithPreloadedDefault(params.tier(), TIER_DEFAULT);
                NativeImage shape =
                        loadTextureWithPreloadedDefault(params.shape(), SHAPE_DEFAULT)) {

            return PixelUtils.Shadow(tier, shape, 0.5f);
        }
    }

    protected record Params(TierReferenceResourceLocation tier,
            ShapeReferenceResourceLocation shape) {
    }
}
