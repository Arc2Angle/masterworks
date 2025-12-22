package com.masterworks.masterworks.data.property.core;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import com.masterworks.masterworks.MasterworksPropertyTypes;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.location.RoleReferenceLocation;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;

public record RenderProperty(Map<Construct.Component.Key, Dynamic<?>> arguments,
        Map<Construct.Component.Key, RoleReferenceLocation> roles) implements Property {

    public Stream<NativeImage> render(
            Map<Construct.Component.Key, Construct.Component> components) {
        return components.entrySet().stream().flatMap(entry -> {
            Construct.Component.Key key = entry.getKey();
            Construct.Component component = entry.getValue();
            RoleReferenceLocation role =
                    Optional.ofNullable(roles.get(key)).orElseThrow(() -> new IllegalStateException(
                            "Missing role for component " + key + " in render property"));
            Dynamic<?> argument = Optional.ofNullable(arguments.get(key))
                    .orElseThrow(() -> new IllegalStateException(
                            "Missing argument for component " + key + " in render property"));

            return role.registered().value().render(role, component, argument);
        });
    }

    @Override
    public Type type() {
        return MasterworksPropertyTypes.RENDER.get();
    }

    public static class Type implements Property.Type<RenderProperty> {
        @Override
        public Decoder<RenderProperty> decoder(
                Map<Construct.Component.Key, RoleReferenceLocation> components) {
            return Decoder.ofSimple(new Decoder.Simple<RenderProperty>() {
                @Override
                public <T> DataResult<RenderProperty> decode(Dynamic<T> input) {
                    return Codec.unboundedMap(Construct.Component.Key.CODEC, Codec.PASSTHROUGH)
                            .parse(input).flatMap(arguments -> {
                                List<Construct.Component.Key> missingKeys = arguments.keySet()
                                        .stream().filter(key -> !components.containsKey(key))
                                        .toList();

                                if (missingKeys.size() > 0) {
                                    return DataResult
                                            .error(() -> "Missing components " + missingKeys);
                                }

                                return DataResult
                                        .success(new RenderProperty(arguments, components));
                            });
                }
            });
        }
    }
}
