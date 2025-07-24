package com.masterworks.masterworks.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.masterworks.masterworks.component.DataComponents;
import com.masterworks.masterworks.material.Material;
import com.masterworks.masterworks.part.type.PartType;
import java.util.function.Consumer;

public class PartItem extends Item {

    // Default constructor for creation a "broken" part
    public PartItem() {
        super(new Item.Properties());
    }

    // Constructor that accepts Item.Properties (for registration)
    public PartItem(Item.Properties properties) {
        super(properties);
    }

    /**
     * Creates a configured PartItem stack with the specified material and part type. This is the
     * main way to create functional parts from the broken template.
     */
    public static ItemStack create(Item partItem, Material material, PartType partType) {
        ItemStack stack = new ItemStack(partItem);
        stack.set(DataComponents.MATERIAL.get(), material);
        stack.set(DataComponents.PART_TYPE.get(), partType);
        return stack;
    }

    /**
     * Returns true if this part has both required components and is functional.
     */
    public static boolean isValid(ItemStack stack) {
        return getMaterial(stack) != null && getPartType(stack) != null;
    }

    /**
     * Gets the material from the item stack, or null if not present.
     */
    @Nullable
    public static Material getMaterial(ItemStack stack) {
        return stack.get(DataComponents.MATERIAL.get());
    }

    /**
     * Gets the part type from the item stack, or null if not present.
     */
    @Nullable
    public static PartType getPartType(ItemStack stack) {
        return stack.get(DataComponents.PART_TYPE.get());
    }

    /**
     * Calculates the effective durability of this part based on material and part type.
     */
    public static int getEffectiveDurability(ItemStack stack) {
        Material material = getMaterial(stack);
        PartType partType = getPartType(stack);

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
        Material material = getMaterial(stack);
        PartType partType = getPartType(stack);

        if (material == null || partType == null) {
            return 0.0f;
        }

        return material.attackDamage() * partType.damageMultiplier();
    }

    /**
     * Calculates the effective attack speed contribution of this part.
     */
    public static float getEffectiveAttackSpeed(ItemStack stack) {
        Material material = getMaterial(stack);
        PartType partType = getPartType(stack);

        if (material == null || partType == null) {
            return 0.0f;
        }

        return material.miningSpeed() * partType.attackSpeedMultiplier();
    }

    /**
     * Adds the tooltip information for this item. This usage is deprecated, but I found no other
     * solution to provide tooltips.
     */
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull Item.TooltipContext context,
            @Nonnull TooltipDisplay display, @Nonnull Consumer<Component> adder,
            @Nonnull TooltipFlag flag) {
        super.appendHoverText(stack, context, display, adder, flag);

        Material material = getMaterial(stack);
        PartType partType = getPartType(stack);

        if (material == null || partType == null) {
            adder.accept(Component.literal("Missing material or part type data!")
                    .withStyle(style -> style.withColor(0xFF0000)));
            return;
        }

        // Add material info
        adder.accept(Component.literal("Material: " + material.name())
                .withStyle(style -> style.withColor(0x55FF55)));

        // Add part type info
        adder.accept(Component.literal("Part Type: " + partType.name())
                .withStyle(style -> style.withColor(0x5555FF)));

        // Add calculated stats
        adder.accept(Component.literal("Effective Durability: " + getEffectiveDurability(stack))
                .withStyle(style -> style.withColor(0xFFFF55)));

        adder.accept(Component
                .literal("Damage Contribution: " + String.format("%.1f", getEffectiveDamage(stack)))
                .withStyle(style -> style.withColor(0xFF5555)));

        adder.accept(Component
                .literal("Attack Speed Contribution: "
                        + String.format("%.1f", getEffectiveAttackSpeed(stack)))
                .withStyle(style -> style.withColor(0xFF55FF)));
    }
}
