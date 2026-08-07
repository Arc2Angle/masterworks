package com.masterworks.masterworks.client.renderer.entity.layers;

import com.masterworks.masterworks.MasterworksDataComponents;
import com.masterworks.masterworks.client.mesh.MeshPoser;
import com.masterworks.masterworks.client.submitter.Submitter;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.Role;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;

public final class ConstructChestplateRenderLayer extends RenderLayer<AvatarRenderState, PlayerModel> {
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
        ItemStack itemStack = state.chestEquipment;
        if (itemStack == null || itemStack.isEmpty()) {
            return;
        }

        Construct argument = itemStack.get(MasterworksDataComponents.CONSTRUCT.get());
        if (argument == null) {
            return;
        }

        Submitter submitter =
                argument.composition().value().roles().get(Role.LAYER).build(argument.components());

        poseStack.pushPose();
        submitter.submit(
                collector,
                new ModelPartMeshPoser(getParentModel().root()),
                poseStack,
                lightCoords,
                OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    private record ModelPartMeshPoser(ModelPart modelPart) implements MeshPoser {
        @Override
        public MeshPoser enter(String name, PoseStack stack) {
            ModelPart child = modelPart.getChild(name);
            child.translateAndRotate(stack);
            return new ModelPartMeshPoser(child);
        }
    }
}
