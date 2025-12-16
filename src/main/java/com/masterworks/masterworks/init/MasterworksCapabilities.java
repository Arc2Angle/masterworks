package com.masterworks.masterworks.init;

import java.util.function.Supplier;
import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.block.entity.ConstructForgeBlockEntity;
import com.masterworks.masterworks.init.registrar.CapabilitiesRegistrar;
import com.mojang.datafixers.util.Unit;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

public class MasterworksCapabilities {
    private static final CapabilitiesRegistrar REGISTRAR =
            new CapabilitiesRegistrar(MasterworksMod.ID);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    static <T, C, BE extends BlockEntity> Unit register(BlockCapability<T, C> capability,
            Supplier<BlockEntityType<BE>> blockEntityType, ICapabilityProvider<BE, C, T> provider) {
        return REGISTRAR.registerBlockEntity(capability, blockEntityType, provider);
    }



    public static final Unit CONSTRUCT_FORGE_INVENTORY =
            register(Capabilities.Item.BLOCK, MasterworksBlockEntityTypes.CONSTRUCT_FORGE,
                    ConstructForgeBlockEntity::inventoryCapability);
}
