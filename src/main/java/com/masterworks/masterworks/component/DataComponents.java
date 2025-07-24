package com.masterworks.masterworks.component;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;
import com.masterworks.masterworks.Masterworks;

public final class DataComponents {

    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister
            .createDataComponents(Registries.DATA_COMPONENT_TYPE, Masterworks.MOD_ID);

    public static final Supplier<DataComponentType<ResourceLocation>> MATERIAL_ITEM =
            DATA_COMPONENTS.registerComponentType("material_item",
                    builder -> builder.networkSynchronized(ResourceLocation.STREAM_CODEC)
                            .persistent(ResourceLocation.CODEC));

    public static final Supplier<DataComponentType<ResourceLocation>> PART_TYPE_ITEM =
            DATA_COMPONENTS.registerComponentType("part_type_item",
                    builder -> builder.networkSynchronized(ResourceLocation.STREAM_CODEC)
                            .persistent(ResourceLocation.CODEC));

    public static void register(IEventBus bus) {
        DATA_COMPONENTS.register(bus);
    }
}
