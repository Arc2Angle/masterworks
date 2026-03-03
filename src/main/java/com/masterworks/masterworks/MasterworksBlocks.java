package com.masterworks.masterworks;

import java.util.function.Function;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MasterworksBlocks {
    private static final DeferredRegister.Blocks REGISTRAR = DeferredRegister.createBlocks(MasterworksMod.ID);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    @SuppressWarnings("unused")
    private static <T extends Block> DeferredBlock<T> register(String name, Function<Block.Properties, T> factory) {
        return REGISTRAR.registerBlock(name, factory);
    }
}
