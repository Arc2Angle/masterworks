package com.masterworks.masterworks;

import java.util.function.Function;
import java.util.function.Supplier;
import com.masterworks.masterworks.item.ConstructItem;
import com.masterworks.masterworks.item.TemplateItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MasterworksItems {
    static final DeferredRegister.Items REGISTRAR = DeferredRegister.createItems(MasterworksMod.ID);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    static <T extends Item> DeferredItem<T> register(String name,
            Function<Item.Properties, T> factory) {
        return REGISTRAR.registerItem(name, factory);
    }

    static DeferredItem<BlockItem> registerBlockItem(String name,
            Supplier<? extends Block> factory) {
        return REGISTRAR.registerSimpleBlockItem(name, factory);
    }



    public static final DeferredItem<ConstructItem> CONSTRUCT =
            register("construct", ConstructItem::new);

    public static final DeferredItem<TemplateItem> TEMPLATE =
            register("template", TemplateItem::new);

    public static final DeferredItem<BlockItem> CONSTRUCT_FORGE =
            registerBlockItem("construct_forge", MasterworksBlocks.CONSTRUCT_FORGE);
}
