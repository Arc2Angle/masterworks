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
import com.masterworks.masterworks.Masterworks;
import com.masterworks.masterworks.component.DataComponents;
import com.masterworks.masterworks.datamap.DataMaps;
import com.masterworks.masterworks.properties.tool.part.material.ToolPartMaterialProperties;
import com.masterworks.masterworks.properties.tool.part.type.ToolPartTypeProperties;
import java.util.function.Consumer;

public class ToolPartItem extends Item {

    public static void init() {}

    private ToolPartItem(Item.Properties properties) {
        super(properties);
    }

    public static final DeferredHolder<Item, ToolPartItem> TOOL_PART =
            Masterworks.ITEMS.registerItem("tool_part", ToolPartItem::new);

    public static void addAllPartItemStacks(java.util.function.Consumer<ItemStack> output) {
        for (ResourceLocation materialItem : BuiltInRegistries.ITEM.keySet()) {
            if (BuiltInRegistries.ITEM.get(materialItem)
                    .map(item -> item.getData(DataMaps.ITEM_TOOL_PART_MATERIAL_PROPERTIES))
                    .isEmpty()) {
                continue;
            }

            for (ResourceLocation typeItem : BuiltInRegistries.ITEM.keySet()) {
                if (BuiltInRegistries.ITEM.get(typeItem)
                        .map(item -> item.getData(DataMaps.ITEM_TOOL_PART_TYPE_PROPERTIES))
                        .isEmpty()) {
                    continue;
                }

                ItemStack partStack = create(TOOL_PART.get(), materialItem, typeItem);
                output.accept(partStack);
            }
        }
    }

    /**
     * Creates a configured PartItem stack with the specified material and part type items. This is
     * the only way to create a valid ToolPartItem stack.
     */
    public static ItemStack create(Item item, ResourceLocation materialItem,
            ResourceLocation typeItem) {
        ItemStack stack = new ItemStack(item);
        stack.set(DataComponents.MATERIAL_ITEM.get(), materialItem);
        stack.set(DataComponents.PART_TYPE_ITEM.get(), typeItem);
        return stack;
    }


    public static record Properties(ToolPartMaterialProperties material,
            ToolPartTypeProperties type) {

        public ToolPartMaterialProperties materialOrDefault() {
            return material != null ? material : ToolPartMaterialProperties.DEFAULT;
        }

        public ToolPartTypeProperties typeOrDefault() {
            return type != null ? type : ToolPartTypeProperties.DEFAULT;
        }
    }

    public static record Construction(ResourceLocation materialItem, ResourceLocation typeItem) {
        public Properties getProperties() {
            ToolPartMaterialProperties materialProperties = BuiltInRegistries.ITEM.get(materialItem)
                    .map(item -> item.getData(DataMaps.ITEM_TOOL_PART_MATERIAL_PROPERTIES))
                    .orElse(null);

            ToolPartTypeProperties typeProperties = BuiltInRegistries.ITEM.get(typeItem)
                    .map(item -> item.getData(DataMaps.ITEM_TOOL_PART_TYPE_PROPERTIES))
                    .orElse(null);

            return new Properties(materialProperties, typeProperties);
        }
    }

    public static Construction getConstruction(ItemStack stack) {
        ResourceLocation materialItem = stack.get(DataComponents.MATERIAL_ITEM.get());
        ResourceLocation typeItem = stack.get(DataComponents.PART_TYPE_ITEM.get());
        return new Construction(materialItem, typeItem);
    }


    public static int getToolPartDurability(ItemStack stack) {
        try {
            Properties properties = getConstruction(stack).getProperties();
            return Math.round(
                    properties.material().durability() * properties.type().durabilityMultiplier());
        } catch (Exception e) {
            Masterworks.LOGGER.error("Failed to calculate durability for stack: {}", stack, e);
            return 0;
        }
    }

    public static float getToolPartDamage(ItemStack stack) {
        try {
            Properties properties = getConstruction(stack).getProperties();
            return properties.material().damage() * properties.type().damageMultiplier();
        } catch (Exception e) {
            Masterworks.LOGGER.error("Failed to calculate damage for stack: {}", stack, e);
            return 0.0f;
        }
    }

    public static float getToolPartActionSpeed(ItemStack stack) {
        try {
            Properties properties = getConstruction(stack).getProperties();
            return properties.material().actionSpeed() * properties.type().actionSpeedMultiplier();
        } catch (Exception e) {
            Masterworks.LOGGER.error("Failed to calculate action speed for stack: {}", stack, e);
            return 1.0f; // Default action speed
        }
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

        try {
            Properties properties = getConstruction(stack).getProperties();
            // Add part type info
            adder.accept(Component.literal("Part Type: " + properties.type().name())
                    .withStyle(style -> style.withColor(0x5555FF)));

            // Add material info
            adder.accept(Component.literal("Material: " + properties.material().name())
                    .withStyle(style -> style.withColor(0x55FF55)));

            // Add calculated stats
            adder.accept(Component.literal("Durability: " + getToolPartDurability(stack))
                    .withStyle(style -> style.withColor(0xFFFF55)));

            adder.accept(
                    Component.literal("Damage: " + String.format("%.1f", getToolPartDamage(stack)))
                            .withStyle(style -> style.withColor(0xFF5555)));

            adder.accept(Component
                    .literal(
                            "Action Speed: " + String.format("%.1f", getToolPartActionSpeed(stack)))
                    .withStyle(style -> style.withColor(0xFF55FF)));
        } catch (Exception e) {

            adder.accept(Component.literal("Missing material or part type data!")
                    .withStyle(style -> style.withColor(0xFF0000)));
            return;
        }
    }
}
