package com.masterworks.masterworks;

import com.masterworks.masterworks.util.Registrar;
import java.util.function.Supplier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class MasterworksCapabilities {
    private static final Registrar<RegisterCapabilitiesEvent> REGISTRAR =
            new Registrar<>(MasterworksMod.ID, RegisterCapabilitiesEvent.class);

    @SuppressWarnings("unused")
    private static <T, C, BE extends BlockEntity> Void register(
            BlockCapability<T, C> capability,
            Supplier<BlockEntityType<BE>> blockEntityType,
            ICapabilityProvider<BE, C, T> provider) {
        REGISTRAR.addUnidentified(event -> event.registerBlockEntity(capability, blockEntityType.get(), provider));
        return null;
    }

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }
}
