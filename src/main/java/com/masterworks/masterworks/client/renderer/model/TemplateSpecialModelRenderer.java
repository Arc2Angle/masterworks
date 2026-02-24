package com.masterworks.masterworks.client.renderer.model;

import com.masterworks.masterworks.MasterworksPreparableReloadListeners;
import com.masterworks.masterworks.client.resource.manager.VoxFileManager;
import com.masterworks.masterworks.client.resource.reference.VoxFileResourceReference;
import com.masterworks.masterworks.util.palette.Palette;
import com.masterworks.masterworks.util.vox.VoxFile;
import com.masterworks.masterworks.util.vox.Voxels;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemStack;

public class TemplateSpecialModelRenderer extends VoxelsSpecialModelRenderer<Void> {
    public record Unbaked(VoxFileResourceReference tier, List<VoxFileResourceReference> shape)
            implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                        VoxFileResourceReference.CODEC.fieldOf("tier").forGetter(Unbaked::tier),
                        Codec.withAlternative(
                                        Codec.list(VoxFileResourceReference.CODEC),
                                        VoxFileResourceReference.CODEC,
                                        List::of)
                                .fieldOf("shape")
                                .forGetter(Unbaked::shape))
                .apply(instance, Unbaked::new));

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        @Nonnull
        public SpecialModelRenderer<?> bake(@Nonnull BakingContext context) {
            VoxFileManager voxFileManager = MasterworksPreparableReloadListeners.VOX_FILE_MANAGER.get();

            VoxFile tierVoxFile = voxFileManager.getOrThrow(tier);
            Voxels tierVoxels = tierVoxFile.voxels(tierVoxFile.palette());

            Voxels shapeVoxels = shape.stream()
                    .map(reference -> voxFileManager.getOrThrow(reference).voxels(Palette.NO_COLOR8))
                    .reduce(Voxels::overlay)
                    .orElseThrow();

            Voxels templateVoxels =
                    etchProjected(tierVoxels, 8, shapeVoxels, 7, 0x7F).compact();

            return new TemplateSpecialModelRenderer(templateVoxels);
        }
    }

    final Voxels voxels;

    TemplateSpecialModelRenderer(Voxels voxels) {
        this.voxels = voxels;
    }

    @Override
    @Nullable
    public Void extractArgument(@Nullable ItemStack stack) {
        return null;
    }

    @Override
    @Nullable
    protected Voxels getVoxels(@Nullable Void argument) {
        return voxels;
    }

    private static Voxels etchProjected(Voxels base, int baseZ, Voxels shape, int shapeZ, int scale) {
        if (base.count().x != shape.count().x || base.count().y != shape.count().y) {
            throw new IllegalArgumentException(
                    "Cannot etch voxels with different x or y counts: " + base.count() + " vs " + shape.count());
        }

        boolean scaleBaseBelow = baseZ > 0;
        boolean scaleBaseAbove = baseZ < base.count().z - 1;

        for (int x = 0; x < base.count().x; x++) {
            for (int y = 0; y < base.count().y; y++) {
                if (!shape.hasColorAt(x, y, shapeZ)) {
                    continue;
                }

                base.setColorAt(x, y, baseZ, 0);

                if (scaleBaseBelow) {
                    base.scaleColorAt(x, y, baseZ - 1, scale);
                }

                if (scaleBaseAbove) {
                    base.scaleColorAt(x, y, baseZ + 1, scale);
                }
            }
        }

        return base;
    }
}
