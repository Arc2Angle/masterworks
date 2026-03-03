package com.masterworks.masterworks;

import com.masterworks.masterworks.util.registrar.CapabilitiesRegistrar;
import com.mojang.datafixers.util.Unit;
import java.util.function.Supplier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

public class MasterworksCapabilities {
    private static final CapabilitiesRegistrar REGISTRAR = new CapabilitiesRegistrar(MasterworksMod.ID);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    @SuppressWarnings("unused")
    private static <T, C, BE extends BlockEntity> Unit register(
            BlockCapability<T, C> capability,
            Supplier<BlockEntityType<BE>> blockEntityType,
            ICapabilityProvider<BE, C, T> provider) {
        return REGISTRAR.registerBlockEntity(capability, blockEntityType, provider);
    }
}
