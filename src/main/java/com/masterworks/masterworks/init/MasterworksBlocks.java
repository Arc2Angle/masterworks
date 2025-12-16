package com.masterworks.masterworks.init;

import java.util.function.Function;
import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.block.ConstructForgeBlock;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MasterworksBlocks {
    static final DeferredRegister.Blocks REGISTRAR =
            DeferredRegister.createBlocks(MasterworksMod.ID);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    static <T extends Block> DeferredBlock<T> register(String name,
            Function<Block.Properties, T> factory) {
        return REGISTRAR.registerBlock(name, factory);
    }


    public static final DeferredBlock<ConstructForgeBlock> CONSTRUCT_FORGE =
            register("construct_forge", ConstructForgeBlock::new);
}
