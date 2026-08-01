package com.masterworks.masterworks.client;

import com.masterworks.masterworks.MasterworksMod;
import com.masterworks.masterworks.MasterworksRegistries;
import com.masterworks.masterworks.client.part.PartDefinition;
import com.masterworks.masterworks.client.part.wavefront.WavefrontPartDefinition;
import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MasterworksPartDefinitionTypes {
    private static final DeferredRegister<PartDefinition.Type<?>> REGISTRAR =
            DeferredRegister.create(MasterworksRegistries.PART_DEFINITION_TYPE, MasterworksMod.ID);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    private static <T extends PartDefinition> Supplier<PartDefinition.Type<T>> register(
            String name, MapCodec<T> codec) {
        return REGISTRAR.register(name, () -> new PartDefinition.Type<>(codec));
    }

    public static final Supplier<PartDefinition.Type<WavefrontPartDefinition>> WAVEFRONT =
            register("wavefront", WavefrontPartDefinition.CODEC);
}
