package com.masterworks.masterworks.client.renderer.model;

import com.masterworks.masterworks.MasterworksDataComponents;
import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.client.MasterworksReloadListeners;
import com.masterworks.masterworks.client.baker.VoxelsBaker;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.role.Role;
import com.masterworks.masterworks.util.vox.Voxels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.QuadCollection;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public record ConstructSpecialModelRenderer(VoxelsBaker baker, QuadCollectionCache cache)
        implements SpecialModelRenderer<Construct> {

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
                    new VoxelsBaker(context.sprites()),
                    MasterworksReloadListeners.CONSTRUCT_SPECIAL_MODEL_RENDERER_CACHE.get());
        }
    }

    public static final class QuadCollectionCache implements ResourceManagerReloadListener {
        private final Map<Construct, QuadCollection> values = new WeakHashMap<>();

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {
            values.clear();
        }

        public void put(Construct construct, QuadCollection quads) {
            values.put(construct, quads);
        }

        @Nullable
        public QuadCollection get(Construct construct) {
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
        Vector3fc extent = new PoseStack.Pose().pose().transformPosition(new Vector3f(0f, 0f, 0f));
        output.accept(extent);
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

        QuadCollection quads = cache.get(argument);
        if (quads == null) {
            try {
                Voxels voxels = argument.composition()
                        .value()
                        .roles()
                        .get(Role.Key.ITEM)
                        .render(argument.components())
                        .reduce(Voxels::overlay)
                        .orElseThrow();

                quads = baker.bake(voxels);
                cache.put(argument, quads);

                MasterworksMod.LOGGER.debug("Baked quads for construct with composition: {}", argument.composition());

            } catch (Exception e) {
                MasterworksMod.LOGGER.error(
                        "Failed to bake quads for construct with composition: {}", argument.composition(), e);
                return;
            }
        }

        collector.submitItem(
                poseStack,
                context,
                packedLight,
                packedOverlay,
                outlineColor,
                null,
                quads.getAll(),
                hasFoil ? ItemStackRenderState.FoilType.STANDARD : ItemStackRenderState.FoilType.NONE);
    }

    // TODO: add a custom packet for a server side reload
}
