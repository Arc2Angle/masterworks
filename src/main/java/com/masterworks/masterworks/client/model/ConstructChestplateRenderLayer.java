package com.masterworks.masterworks.client.model;

import com.masterworks.masterworks.MasterworksDataComponents;
import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.client.vox.VoxAsset;
import com.masterworks.masterworks.client.vox.VoxModel;
import com.masterworks.masterworks.client.vox.VoxQuads;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.role.Role;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Map;
import java.util.SequencedCollection;
import java.util.WeakHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class ConstructChestplateRenderLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
    private final Map<Construct, VoxModel.Baked> cache = new WeakHashMap<>();
    private final VoxQuads.Builder.Factory quadsBuilderFactory =
            VoxQuads.Builder.Factory.ofWhitePixel(Minecraft.getInstance().getAtlasManager());

    public ConstructChestplateRenderLayer(AvatarRenderer<AbstractClientPlayer> renderer) {
        super(renderer);
    }

    @Override
    public void submit(
            PoseStack poseStack,
            SubmitNodeCollector collector,
            int lightCoords,
            AvatarRenderState state,
            float yRot,
            float xRot) {
        ItemStack stack = state.chestEquipment;
        if (stack == null || stack.isEmpty()) {
            return;
        }

        Construct construct = stack.get(MasterworksDataComponents.CONSTRUCT.get());
        if (construct == null) {
            return;
        }

        VoxModel.Baked baked = cache.get(construct);
        if (baked == null) {
            try {
                List<VoxAsset> layers = construct
                        .composition()
                        .value()
                        .roles()
                        .get(Role.Key.ITEM)
                        .render(construct.components())
                        .toList();

                baked = VoxModel.bake(layers, quadsBuilderFactory);
                cache.put(construct, baked);

                MasterworksMod.LOGGER.debug("Baked quads for construct with composition: {}", construct.composition());

            } catch (Exception e) {
                MasterworksMod.LOGGER.error(
                        "Failed to bake quads for construct with composition: {}", construct.composition(), e);
                return;
            }
        }

        baked.submit((path, quadCollection, translation) -> {
            poseStack.pushPose();
            getPart(path).translateAndRotate(poseStack);
            poseStack.translate(translation.x(), translation.y(), translation.z());

            collector.submitItem(
                    poseStack,
                    ItemDisplayContext.NONE,
                    lightCoords,
                    OverlayTexture.NO_OVERLAY,
                    state.outlineColor,
                    null,
                    quadCollection.getAll(),
                    stack.hasFoil() ? ItemStackRenderState.FoilType.STANDARD : ItemStackRenderState.FoilType.NONE);

            poseStack.popPose();
        });
    }

    private ModelPart getPart(SequencedCollection<String> path) {
        ModelPart part = getParentModel().root();

        for (String partName : path) {
            part = part.getChild(partName);
        }

        return part;
    }
}
