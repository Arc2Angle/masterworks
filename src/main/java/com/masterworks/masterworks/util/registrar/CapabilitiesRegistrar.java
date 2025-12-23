package com.masterworks.masterworks.util.registrar;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import com.mojang.datafixers.util.Unit;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class CapabilitiesRegistrar {
    final String namespace;
    final List<Entry<?, ?, ?>> entries = new LinkedList<>();

    public CapabilitiesRegistrar(String namespace) {
        this.namespace = namespace;
    }

    public <T, C, BE extends BlockEntity> Unit registerBlockEntity(BlockCapability<T, C> capability,
            Supplier<BlockEntityType<BE>> blockEntityType, ICapabilityProvider<BE, C, T> provider) {
        entries.add(new Entry<>(capability, blockEntityType, provider));
        return Unit.INSTANCE;
    }

    public void register(IEventBus bus) {
        bus.addListener(this::addEntries);
    }

    private void addEntries(RegisterCapabilitiesEvent event) {
        for (var entry : entries) {
            entry.apply(event);
        }
    }

    record Entry<T, C, BE extends BlockEntity>(BlockCapability<T, C> capability,
            Supplier<BlockEntityType<BE>> blockEntityType, ICapabilityProvider<BE, C, T> provider) {
        void apply(RegisterCapabilitiesEvent event) {
            event.registerBlockEntity(capability, blockEntityType.get(), provider);
        }
    }
}
