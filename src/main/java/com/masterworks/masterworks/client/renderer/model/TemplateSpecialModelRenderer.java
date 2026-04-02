package com.masterworks.masterworks.client.renderer.model;

import com.google.common.base.Suppliers;
import com.masterworks.masterworks.client.MasterworksReloadListeners;
import com.masterworks.masterworks.client.asset.manager.EtchingManager;
import com.masterworks.masterworks.client.asset.manager.VoxModelManager;
import com.masterworks.masterworks.client.etching.Etching;
import com.masterworks.masterworks.client.vox.VoxAsset;
import com.masterworks.masterworks.client.vox.VoxModel;
import com.masterworks.masterworks.client.vox.VoxQuads;
import com.masterworks.masterworks.client.vox.VoxShape;
import com.masterworks.masterworks.typed.identifier.EtchingIdentifier;
import com.masterworks.masterworks.typed.identifier.VoxAssetIdentifier;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public record TemplateSpecialModelRenderer(Supplier<VoxModel.Baked> baked) implements SpecialModelRenderer<Void> {
    public record Unbaked(VoxAssetIdentifier tier, EtchingIdentifier etching) implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                        VoxAssetIdentifier.CODEC.fieldOf("tier").forGetter(Unbaked::tier),
                        EtchingIdentifier.CODEC.fieldOf("etching").forGetter(Unbaked::etching))
                .apply(instance, Unbaked::new));

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        @Nonnull
        public SpecialModelRenderer<?> bake(@Nonnull BakingContext context) {
            // defered to first frame of rendering
            // since baked quads depend on atlas and resource assets

            return new TemplateSpecialModelRenderer(Suppliers.memoize(() -> {
                VoxModelManager voxModelManager = MasterworksReloadListeners.VOX_MODEL_MANAGER.get();
                EtchingManager etchingManager = MasterworksReloadListeners.ETCHING_MANAGER.get();

                VoxAsset tierAsset = tier.assetOrThrow(voxModelManager);
                Etching etchingAsset = etching.assetOrThrow(etchingManager);

                VoxModel model = tierAsset.model().clone();
                VoxShape shape = model.single().orElseThrow();
                shape.etched(etchingAsset, 8, (byte) 0);
                shape.etched(etchingAsset, 7, (byte) 1);

                return model.bake(tierAsset.palette(), VoxQuads.Builder.Factory.ofWhitePixel(context.sprites()));
            }));
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

        baked.get()
                .submit((path, quadCollection) -> collector.submitItem(
                        poseStack,
                        context,
                        packedLight,
                        packedOverlay,
                        outlineColor,
                        null,
                        quadCollection.getAll(),
                        hasFoil ? ItemStackRenderState.FoilType.STANDARD : ItemStackRenderState.FoilType.NONE));
    }
}
