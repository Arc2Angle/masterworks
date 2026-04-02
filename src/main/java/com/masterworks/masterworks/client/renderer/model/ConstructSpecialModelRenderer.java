package com.masterworks.masterworks.client.renderer.model;

import com.masterworks.masterworks.MasterworksDataComponents;
import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.client.MasterworksReloadListeners;
import com.masterworks.masterworks.client.vox.VoxAsset;
import com.masterworks.masterworks.client.vox.VoxModel;
import com.masterworks.masterworks.client.vox.VoxQuads;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.role.Role;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class ConstructSpecialModelRenderer implements SpecialModelRenderer<Construct> {
    private final VoxQuads.Builder.Factory quadsBuilderFactory;
    private final Cache cache;

    public ConstructSpecialModelRenderer(VoxQuads.Builder.Factory quadsBuilderFactory, Cache cache) {
        this.quadsBuilderFactory = quadsBuilderFactory;
        this.cache = cache;
    }

    public static final class Unbaked implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<ConstructSpecialModelRenderer.Unbaked> MAP_CODEC =
                MapCodec.unit(ConstructSpecialModelRenderer.Unbaked::new);

        @Override
        public MapCodec<ConstructSpecialModelRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        @Nonnull
        public SpecialModelRenderer<?> bake(@Nonnull BakingContext context) {
            return new ConstructSpecialModelRenderer(
                    VoxQuads.Builder.Factory.ofWhitePixel(context.sprites()),
                    MasterworksReloadListeners.CONSTRUCT_SPECIAL_MODEL_RENDERER_CACHE.get());
        }
    }

    public static final class Cache implements ResourceManagerReloadListener {
        private final Map<Construct, VoxModel.Baked> values = new WeakHashMap<>();

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {
            values.clear();
        }

        public void put(Construct construct, VoxModel.Baked baked) {
            values.put(construct, baked);
        }

        @Nullable
        public VoxModel.Baked get(Construct construct) {
            return values.get(construct);
        }
    }

    @Override
    @Nullable
    public Construct extractArgument(@Nullable ItemStack stack) {
        if (stack == null) {
            return null;
        }

        return stack.get(MasterworksDataComponents.CONSTRUCT.get());
    }

    @Override
    public void getExtents(@Nonnull Consumer<Vector3fc> output) {
        output.accept(new Vector3f());
    }

    @Override
    public void submit(
            @Nullable Construct argument,
            @Nonnull ItemDisplayContext context,
            @Nonnull PoseStack poseStack,
            @Nonnull SubmitNodeCollector collector,
            int packedLight,
            int packedOverlay,
            boolean hasFoil,
            int outlineColor) {
        if (argument == null) {
            return;
        }

        VoxModel.Baked baked = cache.get(argument);
        if (baked == null) {
            try {
                List<VoxAsset> layers = argument.composition()
                        .value()
                        .roles()
                        .get(Role.Key.ITEM)
                        .render(argument.components())
                        .toList();

                baked = VoxModel.bake(layers, quadsBuilderFactory);
                cache.put(argument, baked);

                MasterworksMod.LOGGER.debug("Baked quads for construct with composition: {}", argument.composition());

            } catch (Exception e) {
                MasterworksMod.LOGGER.error(
                        "Failed to bake quads for construct with composition: {}", argument.composition(), e);
                return;
            }
        }

        baked.submit((path, quadCollection, translation) -> {
            poseStack.pushPose();
            poseStack.translate(translation.x(), translation.y(), translation.z());

            collector.submitItem(
                    poseStack,
                    context,
                    packedLight,
                    packedOverlay,
                    outlineColor,
                    null,
                    quadCollection.getAll(),
                    hasFoil ? ItemStackRenderState.FoilType.STANDARD : ItemStackRenderState.FoilType.NONE);

            poseStack.popPose();
        });
    }

    // TODO: add a custom packet for a server side reload
}
