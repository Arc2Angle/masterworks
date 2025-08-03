package com.masterworks.masterworks.item;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.neoforge.registries.DeferredHolder;
import javax.annotation.Nonnull;
import com.masterworks.masterworks.Masterworks;
import com.masterworks.masterworks.data.construct.Construct;
import com.masterworks.masterworks.resource.location.MaterialResourceLocation;
import com.masterworks.masterworks.resource.location.TemplateResourceLocation;
import com.mojang.datafixers.util.Either;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConstructItem extends Item {

    public static void init() {}

    private ConstructItem(Item.Properties properties) {
        super(properties);
    }

    public static final DeferredHolder<Item, ConstructItem> ITEM =
            Masterworks.ITEMS.registerItem("construct", ConstructItem::new);

    public static final Supplier<CreativeModeTab> PARTS_TAB =
            Masterworks.CREATIVE_MODE_TABS.register("constructs", () -> CreativeModeTab.builder()
                    .title(Component
                            .translatable("itemGroup." + Masterworks.MOD_ID + ".constructs"))
                    .icon(() -> new ItemStack(ConstructItem.ITEM.get()))
                    .displayItems((params, output) -> {
                        output.accept(createExampleSword());
                    }).build());


    /**
     * Creates a configured ItemStack with the specified material and part type items. This is the
     * only way to create a valid ConstructItem stack.
     */
    public static ItemStack create(Item item, Construct construct) {
        ItemStack stack = new ItemStack(item);
        stack.set(Construct.DATA_COMPONENT.get(), construct);
        return stack;
    }

    private static ItemStack createExampleSword() {
        MaterialResourceLocation wood = new MaterialResourceLocation(
                ResourceLocation.fromNamespaceAndPath("minecraft", "oak_planks"));
        MaterialResourceLocation iron = new MaterialResourceLocation(
                ResourceLocation.fromNamespaceAndPath("minecraft", "iron_ingot"));
        MaterialResourceLocation diamond = new MaterialResourceLocation(
                ResourceLocation.fromNamespaceAndPath("minecraft", "diamond"));
        MaterialResourceLocation emerald = new MaterialResourceLocation(
                ResourceLocation.fromNamespaceAndPath("minecraft", "emerald"));

        TemplateResourceLocation swordBladeTemplate =
                new TemplateResourceLocation(Masterworks.resourceLocation("sword_blade_template"));
        TemplateResourceLocation bindingTemplate =
                new TemplateResourceLocation(Masterworks.resourceLocation("binding_template"));
        TemplateResourceLocation rodTemplate =
                new TemplateResourceLocation(Masterworks.resourceLocation("rod_template"));
        TemplateResourceLocation swordTemplate =
                new TemplateResourceLocation(Masterworks.resourceLocation("sword_template"));

        Construct leftEdge = new Construct(swordBladeTemplate, 0, List.of(Either.left(diamond)));
        Construct rightEdge = new Construct(swordBladeTemplate, 0, List.of(Either.left(emerald)));
        Construct swordBlade = new Construct(swordBladeTemplate, 1,
                List.of(Either.right(leftEdge), Either.right(rightEdge)));

        Construct rod = new Construct(rodTemplate, 0, List.of(Either.left(wood)));
        Construct binding = new Construct(bindingTemplate, 0, List.of(Either.left(iron)));

        Construct sword = new Construct(swordTemplate, 1,
                List.of(Either.right(swordBlade), Either.right(binding), Either.right(rod)));

        return create(ConstructItem.ITEM.get(), sword);
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

        adder.accept(Component.literal("Description here")
                .withStyle(style -> style.withColor(0xFF0000)));
    }
}
