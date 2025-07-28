package com.masterworks.masterworks.data;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;
import com.masterworks.masterworks.Masterworks;
import com.masterworks.masterworks.data.construct.Construct;

public class Components {
    public static void init() {}

    private static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister
            .createDataComponents(Registries.DATA_COMPONENT_TYPE, Masterworks.MOD_ID);

    public static final Supplier<DataComponentType<Construct>> CONSTRUCT =
            REGISTRAR.registerComponentType("construct", builder -> builder
                    .networkSynchronized(Construct.STREAM_CODEC).persistent(Construct.CODEC));
}
