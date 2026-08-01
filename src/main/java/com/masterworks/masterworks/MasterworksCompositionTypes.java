package com.masterworks.masterworks;

import com.masterworks.masterworks.data.composition.AbstractComposition;
import com.masterworks.masterworks.data.composition.Composition;
import com.masterworks.masterworks.data.composition.ConcreteComposition;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MasterworksCompositionTypes {
    private static final DeferredRegister<Composition.Type<?>> REGISTRAR =
            DeferredRegister.create(MasterworksRegistries.COMPOSITION_TYPE, MasterworksMod.ID);

    public static void register(IEventBus bus) {
        REGISTRAR.register(bus);
    }

    private static <A extends Composition> Supplier<Composition.Type<A>> register(String path, MapDecoder<A> decoder) {
        return REGISTRAR.register(
                path,
                () -> new Composition.Type<>(new MapCodec<A>() {
                    @Override
                    public <T> Stream<T> keys(DynamicOps<T> ops) {
                        return decoder.keys(ops);
                    }

                    @Override
                    public <T> DataResult<A> decode(DynamicOps<T> ops, MapLike<T> input) {
                        return decoder.decode(ops, input);
                    }

                    @Override
                    public <T> RecordBuilder<T> encode(A input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                        throw new UnsupportedOperationException("Compositions do not support encoding");
                    }
                }));
    }

    public static final Supplier<Composition.Type<AbstractComposition>> ABSTRACT =
            register("abstract", AbstractComposition.DECODER);

    public static final Supplier<Composition.Type<ConcreteComposition>> CONCRETE =
            register("concrete", ConcreteComposition.DECODER);
}
