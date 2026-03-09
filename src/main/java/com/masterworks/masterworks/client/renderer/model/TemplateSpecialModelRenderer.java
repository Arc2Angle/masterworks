package com.masterworks.masterworks.client.renderer.model;

import com.google.common.base.Suppliers;
import com.masterworks.masterworks.client.MasterworksReloadListeners;
import com.masterworks.masterworks.client.asset.manager.VoxFileManager;
import com.masterworks.masterworks.client.baker.VoxelsBaker;
import com.masterworks.masterworks.typed.identifier.VoxFileIdentifier;
import com.masterworks.masterworks.util.palette.Palette;
import com.masterworks.masterworks.util.vox.VoxFile;
import com.masterworks.masterworks.util.vox.Voxels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public record TemplateSpecialModelRenderer(Supplier<List<BakedQuad>> bakedQuadsSupplier)
        implements SpecialModelRenderer<Void> {
    public record Unbaked(VoxFileIdentifier tier, List<VoxFileIdentifier> shape)
            implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                        VoxFileIdentifier.CODEC.fieldOf("tier").forGetter(Unbaked::tier),
                        Codec.withAlternative(Codec.list(VoxFileIdentifier.CODEC), VoxFileIdentifier.CODEC, List::of)
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
            VoxFileManager voxFileManager = MasterworksReloadListeners.VOX_FILE_MANAGER.get();

            VoxFile tierVoxFile = tier.assetOrThrow(voxFileManager);
            Voxels tierVoxels = tierVoxFile.voxels(tierVoxFile.palette());

            Voxels shapeVoxels = shape.stream()
                    .map(reference -> reference.assetOrThrow(voxFileManager).voxels(Palette.NO_COLOR8))
                    .reduce(Voxels::overlay)
                    .orElseThrow();

            Voxels templateVoxels = etchProjected(tierVoxels, 8, shapeVoxels, 7, 0x7F);

            VoxelsBaker baker = new VoxelsBaker(context.sprites());
            return new TemplateSpecialModelRenderer(Suppliers.memoize(() -> baker.bake(templateVoxels)));
        }
    }

    @Override
    @Nullable
    public Void extractArgument(@Nullable ItemStack stack) {
        return null;
    }

    @Override
    public void getExtents(@Nonnull Consumer<Vector3fc> output) {
        Vector3fc extent = new PoseStack.Pose().pose().transformPosition(new Vector3f(0f, 0f, 0f));
        output.accept(extent);
    }

    @Override
    public void submit(
            @Nullable Void argument,
            @Nonnull ItemDisplayContext context,
            @Nonnull PoseStack poseStack,
            @Nonnull SubmitNodeCollector collector,
            int packedLight,
            int packedOverlay,
            boolean hasFoil,
            int outlineColor) {

        collector.submitItem(
                poseStack,
                context,
                packedLight,
                packedOverlay,
                outlineColor,
                null,
                bakedQuadsSupplier.get(),
                hasFoil ? ItemStackRenderState.FoilType.STANDARD : ItemStackRenderState.FoilType.NONE);
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
