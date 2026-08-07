package com.masterworks.masterworks.client.renderer.special;

import com.masterworks.masterworks.MasterworksDataComponents;
import com.masterworks.masterworks.client.mesh.poser.TrivialMeshPoser;
import com.masterworks.masterworks.client.submitter.Submitter;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.Role;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class ConstructSpecialModelRenderer implements SpecialModelRenderer<Construct> {

    public static final class Unbaked implements SpecialModelRenderer.Unbaked<Construct> {
        public static final MapCodec<ConstructSpecialModelRenderer.Unbaked> MAP_CODEC =
                MapCodec.unit(ConstructSpecialModelRenderer.Unbaked::new);

        @Override
        public MapCodec<ConstructSpecialModelRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        @Nonnull
        public SpecialModelRenderer<Construct> bake(@Nonnull BakingContext context) {
            return new ConstructSpecialModelRenderer();
        }
    }

    @Override
    @Nullable
    public Construct extractArgument(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }

        return itemStack.get(MasterworksDataComponents.CONSTRUCT.get());
    }

    @Override
    public void getExtents(@Nonnull Consumer<Vector3fc> output) {
        output.accept(new Vector3f());
    }

    @Override
    public void submit(
            @Nullable Construct argument,
            @Nonnull PoseStack poseStack,
            @Nonnull SubmitNodeCollector collector,
            int packedLight,
            int packedOverlay,
            boolean hasFoil,
            int outlineColor) {
        if (argument == null) {
            return;
        }

        Submitter submitter =
                argument.composition().value().roles().get(Role.ITEM).build(argument.components());

        poseStack.pushPose();
        poseStack.translate(0, 0, 0.5);
        // poseStack.scale(-1, 1, 1);
        submitter.submit(collector, new TrivialMeshPoser(), poseStack, packedLight, packedOverlay);
        poseStack.popPose();
    }
}
