package com.masterworks.masterworks.client.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import com.masterworks.masterworks.component.DataComponents;

/**
 * ItemTintSource that provides material-based coloring for tool parts. This reads the material from
 * the ItemStack's DataComponents and maps it to colors from the corresponding 8x1 material palette
 * textures.
 */
public class MaterialTintSource implements ItemTintSource {

    // The MapCodec for registration (no configuration needed, so unit codec)
    public static final MapCodec<MaterialTintSource> MAP_CODEC =
            MapCodec.unit(new MaterialTintSource());

    // Default color for broken/missing materials (neutral gray)
    private static final int DEFAULT_COLOR = ARGB.opaque(0x808080); // Gray

    // Material color mappings - these should match your material palette textures
    // TODO: Load complex texture from file
    private static final java.util.Map<String, Integer> MATERIAL_COLORS =
            java.util.Map.of("minecraft:wood", ARGB.opaque(0x8B4513), // Saddle brown
                    "minecraft:stone", ARGB.opaque(0x808080), // Gray
                    "minecraft:iron_ingot", ARGB.opaque(0xC0C0C0), // Silver
                    "minecraft:gold_ingot", ARGB.opaque(0xFFD700), // Gold
                    "minecraft:diamond", ARGB.opaque(0x87CEEB) // Sky blue
            );

    @Override
    public int calculate(@Nonnull ItemStack stack, @Nullable ClientLevel level,
            @Nullable LivingEntity entity) {
        // Get the material ResourceLocation from the item's data components
        ResourceLocation materialId = stack.get(DataComponents.MATERIAL_ITEM.get());

        // If no material is set, return default color
        if (materialId == null) {
            return DEFAULT_COLOR;
        }

        // Look up the color for this material
        String materialKey = materialId.toString();
        Integer color = MATERIAL_COLORS.get(materialKey);

        // Return the material color, or default if not found
        return color != null ? color : DEFAULT_COLOR;
    }

    @Override
    public MapCodec<MaterialTintSource> type() {
        return MAP_CODEC;
    }
}
