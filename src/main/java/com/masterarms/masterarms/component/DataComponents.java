package com.masterarms.masterarms.component;

import com.masterarms.masterarms.Masterarms;
import com.masterarms.masterarms.material.Material;
import com.masterarms.masterarms.part.type.PartType;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class DataComponents {

    private static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister
            .createDataComponents(Registries.DATA_COMPONENT_TYPE, Masterarms.MODID);

    /**
     * The deferred register for data components.
     */
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister
            .create(Registries.DATA_COMPONENT_TYPE, Masterarms.MODID);

    /**
     * A component that holds the {@link PartType} of a tool part.
     */
    public static final Supplier<DataComponentType<PartType>> PART_TYPE = REGISTRAR
            .registerComponentType("part_type", builder -> builder
                    .persistent(PartType.CODEC));

    /**
     * A component that holds the {@link Material} of a tool part.
     */
    public static final Supplier<DataComponentType<Material>> MATERIAL = REGISTRAR
            .registerComponentType("material", builder -> builder
                    .persistent(Material.CODEC));

    public static void register(IEventBus bus) {
        DATA_COMPONENTS.register(bus);
    }
}
