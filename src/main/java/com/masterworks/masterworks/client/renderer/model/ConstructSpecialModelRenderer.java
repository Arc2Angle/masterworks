package com.masterworks.masterworks.client.renderer.model;

import com.masterworks.masterworks.MasterworksDataComponents;
import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.MasterworksPropertyTypes;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.core.RenderProperty;
import com.masterworks.masterworks.location.RoleReferenceLocation;
import com.masterworks.masterworks.util.vox.Voxels;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemStack;

public class ConstructSpecialModelRenderer extends VoxelsSpecialModelRenderer<Construct> {
    public record Unbaked() implements SpecialModelRenderer.Unbaked {
        public static final MapCodec<ConstructSpecialModelRenderer.Unbaked> MAP_CODEC =
                MapCodec.unit(ConstructSpecialModelRenderer.Unbaked::new);

        @Override
        public MapCodec<ConstructSpecialModelRenderer.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        @Nonnull
        public SpecialModelRenderer<?> bake(@Nonnull BakingContext context) {
            return new ConstructSpecialModelRenderer();
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

    private Map<Construct, Voxels> cache = new WeakHashMap<>();

    @Override
    @Nullable
    protected Voxels getVoxels(@Nullable Construct argument) {
        if (argument == null) {
            return null;
        }

        Voxels cached = cache.get(argument);
        if (cached != null) {
            return cached;
        }

        RenderProperty property = argument.properties(RoleReferenceLocation.ITEM)
                .get(MasterworksPropertyTypes.RENDER.get())
                .orElse(null);

        if (property == null) {
            MasterworksMod.LOGGER.warn("Missing render property for construct " + argument);
            return null;
        }

        try {
            Voxels voxels = property.render(argument.components())
                    .reduce(Voxels::overlay)
                    .orElseThrow()
                    .compact();

            cache.put(argument, voxels);
            return voxels;
        } catch (Exception e) {
            throw new RuntimeException("Failed to render composition " + argument.composition(), e);
        }
    }
}
