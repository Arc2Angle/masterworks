package com.masterworks.masterworks.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.neoforge.registries.DeferredHolder;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.masterworks.masterworks.Masterworks;
import com.masterworks.masterworks.component.DataComponents;
import com.masterworks.masterworks.datamap.DataMaps;
import com.masterworks.masterworks.material.MaterialProperties;
import com.masterworks.masterworks.part.type.PartTypeProperties;
import java.util.function.Consumer;

public class PartItem extends Item {

    public static void init() {}

    // Constructor that accepts Item.Properties (for registration)
    private PartItem(Item.Properties properties) {
        super(properties);
    }

    public static final DeferredHolder<Item, PartItem> PART =
            Masterworks.ITEMS.registerItem("part", PartItem::new);

    public static void addAllParts(java.util.function.Consumer<ItemStack> output) {
        // Get all items that have material properties defined
        for (ResourceLocation itemId : BuiltInRegistries.ITEM.keySet()) {
            var itemHolder = BuiltInRegistries.ITEM.get(itemId);
            if (itemHolder.isEmpty())
                continue;

            // Check if this item has material properties (can be used as a material)
            MaterialProperties materialProps =
                    itemHolder.get().getData(DataMaps.ITEM_MATERIAL_PROPERTIES);
            if (materialProps == null)
                continue;

            // Get all items that have part type properties defined
            for (ResourceLocation partTypeItemId : BuiltInRegistries.ITEM.keySet()) {
                var partTypeHolder = BuiltInRegistries.ITEM.get(partTypeItemId);
                if (partTypeHolder.isEmpty())
                    continue;

                // Check if this item has part type properties (can be used as a part type)
                PartTypeProperties partTypeProps =
                        partTypeHolder.get().getData(DataMaps.ITEM_PART_TYPE_PROPERTIES);
                if (partTypeProps == null)
                    continue;

                // Create a part with this material and part type combination
                ItemStack partStack = create(PART.get(), itemId, partTypeItemId);
                output.accept(partStack);
            }
        }
    }

    /**
     * Creates a configured PartItem stack with the specified material and part type items. This is
     * the main way to create functional parts from the broken template.
     */
    public static ItemStack create(Item partItem, ResourceLocation materialItemId,
            ResourceLocation partTypeItemId) {
        ItemStack stack = new ItemStack(partItem);
        stack.set(DataComponents.MATERIAL_ITEM.get(), materialItemId);
        stack.set(DataComponents.PART_TYPE_ITEM.get(), partTypeItemId);
        return stack;
    }

    /**
     * Creates a configured PartItem stack using Item objects (convenience method).
     */
    public static ItemStack create(Item partItem, Item materialItem, Item partTypeItem) {
        return create(partItem, BuiltInRegistries.ITEM.getKey(materialItem),
                BuiltInRegistries.ITEM.getKey(partTypeItem));
    }

    /**
     * Returns true if this part has both required components and is functional.
     */
    public static boolean isValid(ItemStack stack) {
        return getMaterial(stack) != null && getPartType(stack) != null;
    }

    /**
     * Gets the material properties from the item stack, or null if not present.
     */
    @Nullable
    public static MaterialProperties getMaterial(ItemStack stack) {
        ResourceLocation materialItemId = stack.get(DataComponents.MATERIAL_ITEM.get());
        if (materialItemId == null) {
            return null;
        }

        var itemHolder = BuiltInRegistries.ITEM.get(materialItemId);
        if (itemHolder.isEmpty()) {
            return MaterialProperties.DEFAULT;
        }

        MaterialProperties props = itemHolder.get().getData(DataMaps.ITEM_MATERIAL_PROPERTIES);
        return props != null ? props : MaterialProperties.DEFAULT;
    }

    /**
     * Gets the part type properties from the item stack, or null if not present.
     */
    @Nullable
    public static PartTypeProperties getPartType(ItemStack stack) {
        ResourceLocation partTypeItemId = stack.get(DataComponents.PART_TYPE_ITEM.get());
        if (partTypeItemId == null) {
            return null;
        }

        var itemHolder = BuiltInRegistries.ITEM.get(partTypeItemId);
        if (itemHolder.isEmpty()) {
            return null;
        }

        PartTypeProperties props = itemHolder.get().getData(DataMaps.ITEM_PART_TYPE_PROPERTIES);
        return props; // Return null if no properties found
    }

    /**
     * Calculates the effective durability of this part based on material and part type.
     */
    public static int getEffectiveDurability(ItemStack stack) {
        MaterialProperties material = getMaterial(stack);
        PartTypeProperties partType = getPartType(stack);

        // Return 0 for broken/incomplete parts instead of crashing
        if (material == null || partType == null) {
            return 0;
        }

        return Math.round(material.durability() * partType.durabilityMultiplier());
    }

    /**
     * Calculates the effective damage contribution of this part.
     */
    public static float getEffectiveDamage(ItemStack stack) {
        MaterialProperties material = getMaterial(stack);
        PartTypeProperties partType = getPartType(stack);

        if (material == null || partType == null) {
            return 0.0f;
        }

        return material.attackDamage() * partType.damageMultiplier();
    }

    /**
     * Calculates the effective attack/mining speed contribution of this part.
     */
    public static float getEffectiveActionSpeed(ItemStack stack) {
        MaterialProperties material = getMaterial(stack);
        PartTypeProperties partType = getPartType(stack);

        if (material == null || partType == null) {
            return 0.0f;
        }

        return material.actionSpeed() * partType.actionSpeedMultiplier();
    }

    /**
     * Adds the tooltip information for this item.
     * 
     * Note: `appendHoverText` is currently deprecated, but no other solution exists.
     */
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull Item.TooltipContext context,
            @Nonnull TooltipDisplay display, @Nonnull Consumer<Component> adder,
            @Nonnull TooltipFlag flag) {
        super.appendHoverText(stack, context, display, adder, flag);

        MaterialProperties material = getMaterial(stack);
        PartTypeProperties partType = getPartType(stack);

        if (material == null || partType == null) {
            adder.accept(Component.literal("Missing material or part type data!")
                    .withStyle(style -> style.withColor(0xFF0000)));
            return;
        }

        // Add part type info
        adder.accept(Component.literal("Part Type: " + partType.name())
                .withStyle(style -> style.withColor(0x5555FF)));

        // Add material info
        adder.accept(Component.literal("Material: " + material.name())
                .withStyle(style -> style.withColor(0x55FF55)));

        // Add calculated stats
        adder.accept(Component.literal("Durability: " + getEffectiveDurability(stack))
                .withStyle(style -> style.withColor(0xFFFF55)));

        adder.accept(
                Component.literal("Damage: " + String.format("%.1f", getEffectiveDamage(stack)))
                        .withStyle(style -> style.withColor(0xFF5555)));

        adder.accept(Component
                .literal("Action Speed: " + String.format("%.1f", getEffectiveActionSpeed(stack)))
                .withStyle(style -> style.withColor(0xFF55FF)));
    }
}
