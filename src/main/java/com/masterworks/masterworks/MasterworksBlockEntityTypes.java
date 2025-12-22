package com.masterworks.masterworks;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.masterworks.masterworks.block.entity.ConstructForgeBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MasterworksBlockEntityTypes {
    static final DeferredRegister<BlockEntityType<?>> REGISTRAR =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MasterworksMod.ID);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    static <T extends BlockEntity> Supplier<BlockEntityType<T>> register(String name,
            BlockEntitySupplier<T> factory,
            Stream<? extends Supplier<? extends Block>> validBlocks) {
        return REGISTRAR.register(name, () -> new BlockEntityType<>(factory,
                validBlocks.map(Supplier::get).collect(Collectors.toSet())));
    }

    @SafeVarargs
    static <T extends BlockEntity> Supplier<BlockEntityType<T>> register(String name,
            BlockEntitySupplier<T> factory, Supplier<? extends Block>... validBlocks) {
        return register(name, factory, Arrays.stream(validBlocks));
    }


    public static final Supplier<BlockEntityType<ConstructForgeBlockEntity>> CONSTRUCT_FORGE =
            register("construct_forge", ConstructForgeBlockEntity::new,
                    MasterworksBlocks.CONSTRUCT_FORGE);
}
