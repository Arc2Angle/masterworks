package com.masterworks.masterworks.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.neoforge.registries.DeferredHolder;
import javax.annotation.Nonnull;
import com.masterworks.masterworks.Masterworks;
import com.masterworks.masterworks.data.construct.Construct;
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
        Construct sword = new Construct(Masterworks.resourceLocation("sword"), 0,
                List.of(Either.left(Masterworks.resourceLocation("iron"))));

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
