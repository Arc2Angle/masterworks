package com.masterworks.masterworks.component;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;
import com.masterworks.masterworks.Masterworks;
import com.masterworks.masterworks.material.Material;
import com.masterworks.masterworks.part.type.PartType;

public final class DataComponents {

        public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister
                        .createDataComponents(Registries.DATA_COMPONENT_TYPE, Masterworks.MOD_ID);

        public static final Supplier<DataComponentType<PartType>> PART_TYPE =
                        DATA_COMPONENTS.registerComponentType("part_type",
                                        builder -> builder.persistent(PartType.CODEC));

        public static final Supplier<DataComponentType<Material>> MATERIAL =
                        DATA_COMPONENTS.registerComponentType("material",
                                        builder -> builder.persistent(Material.CODEC));

        public static void register(IEventBus bus) {
                DATA_COMPONENTS.register(bus);
        }
}
