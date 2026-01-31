package com.masterworks.masterworks;

import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.Template;
import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MasterworksDataComponents {
    static final DeferredRegister.DataComponents REGISTRAR =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MasterworksMod.ID);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    static <T> Supplier<DataComponentType<T>> register(
            String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        return REGISTRAR.registerComponentType(name, builder);
    }

    static <T> Supplier<DataComponentType<T>> register(
            String name, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        return REGISTRAR.registerComponentType(
                name, builder -> builder.networkSynchronized(streamCodec).persistent(codec));
    }

    public static final Supplier<DataComponentType<Construct>> CONSTRUCT =
            register("construct", Construct.CODEC, Construct.STREAM_CODEC);

    public static final Supplier<DataComponentType<Template>> TEMPLATE =
            register("template", Template.CODEC, Template.STREAM_CODEC);
}
