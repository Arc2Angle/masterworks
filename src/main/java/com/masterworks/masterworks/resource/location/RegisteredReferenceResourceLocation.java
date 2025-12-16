package com.masterworks.masterworks.resource.location;

import java.util.function.Function;
import com.masterworks.masterworks.util.ReferenceDispatchedMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public interface RegisteredReferenceResourceLocation<T> extends ReferenceResourceLocation {
    Registry<T> registry();

    public default Holder.Reference<T> registered() {
        Registry<T> registry = registry();
        ResourceLocation key = value();
        return registry.get(key).orElseThrow(() -> new MissingRegistryKeyException(key, registry));
    }

    public static class MissingRegistryKeyException extends RuntimeException {
        public MissingRegistryKeyException(ResourceLocation key, Registry<?> registry) {
            super("Missing " + key + " in registry " + registry);
        }
    }

    public static <K, R extends RegisteredReferenceResourceLocation<K>, V> Codec<ReferenceDispatchedMap<R, K, V>> dispatchedMapCodec(
            Codec<R> referenceCodec,
            Function<? super K, ? extends Decoder<? extends V>> toDecoder) {
        return ReferenceDispatchedMap.codec(referenceCodec,
                reference -> reference.registered().value(), toDecoder);
    }
}
