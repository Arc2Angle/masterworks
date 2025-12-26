package com.masterworks.masterworks.client.item;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;
import com.masterworks.masterworks.MasterworksDataComponents;
import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.MasterworksPropertyTypes;
import com.masterworks.masterworks.client.draw.PixelUtils;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.core.RenderEquipmentProperty;
import com.masterworks.masterworks.location.RoleReferenceLocation;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class ConstructClientItemExtensions implements IClientItemExtensions, Closeable {
    final Map<Construct, ResourceLocation> locations = new HashMap<>();

    @Override
    public @Nullable ResourceLocation getArmorTexture(ItemStack stack,
            EquipmentClientInfo.LayerType type, EquipmentClientInfo.Layer layer,
            ResourceLocation _default) {
        Construct construct = stack.get(MasterworksDataComponents.CONSTRUCT);
        if (construct == null) {
            return _default;
        }

        ResourceLocation cached = locations.get(construct);
        if (cached != null) {
            return cached;
        }

        RenderEquipmentProperty property = construct.properties(RoleReferenceLocation.ITEM)
                .get(MasterworksPropertyTypes.RENDER_EQUIPMENT.get()).orElse(null);
        if (property == null) {
            MasterworksMod.LOGGER.warn("Missing render property for construct " + construct);
            return _default;
        }

        NativeImage image =
                property.render(construct.components()).reduce(PixelUtils::Overlay).orElse(null);
        if (image == null) {
            MasterworksMod.LOGGER.warn("Failed to render image for construct " + construct);
            return _default;
        }

        String label = UUID.randomUUID().toString();
        ResourceLocation location = qualify(label);
        Minecraft.getInstance().getTextureManager().register(location,
                new DynamicTexture(() -> label, image));

        locations.put(construct, location);
        return location;
    }

    private static ResourceLocation qualify(String label) {
        return ResourceLocation.fromNamespaceAndPath(MasterworksMod.ID,
                "assets/" + MasterworksMod.ID + "/textures/generated/" + label + ".png");
    }

    @Override
    public void close() {
        for (ResourceLocation location : locations.values()) {
            Minecraft.getInstance().getTextureManager().release(location);
        }
        locations.clear();
    }
}
