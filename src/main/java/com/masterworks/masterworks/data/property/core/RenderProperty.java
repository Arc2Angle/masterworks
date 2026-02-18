package com.masterworks.masterworks.data.property.core;

import com.masterworks.masterworks.client.resource.reference.VoxFileResourceReference;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.location.RoleReferenceLocation;
import com.masterworks.masterworks.util.vox.Voxels;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public record RenderProperty(
        List<Construct.Component.Key> keys,
        Map<Construct.Component.Key, Optional<Dynamic<?>>> arguments,
        Map<Construct.Component.Key, RoleReferenceLocation> roles)
        implements Property {

    public Stream<Voxels> render(Map<Construct.Component.Key, Construct.Component> components) {
        return keys.stream().flatMap(key -> {
            Construct.Component component = Optional.ofNullable(components.get(key))
                    .orElseThrow(() -> new IllegalStateException("Missing component " + key + " in render property"));

            RoleReferenceLocation role = Optional.ofNullable(roles.get(key))
                    .orElseThrow(() ->
                            new IllegalStateException("Missing role for component " + key + " in render property"));

            Optional<Dynamic<?>> argument = Optional.ofNullable(arguments.get(key))
                    .orElseThrow(() ->
                            new IllegalStateException("Missing argument for component " + key + " in render property"));

            return role.registered().value().render(role, component, argument);
        });
    }

    @Override
    public Type type() {
        return null;
    }

    public static final class Type implements Property.Type<RenderProperty> {
        @Override
        public Decoder<RenderProperty> decoder(Map<Construct.Component.Key, RoleReferenceLocation> components) {
            return Decoder.ofSimple(new Decoder.Simple<RenderProperty>() {
                @Override
                public <T> DataResult<RenderProperty> decode(Dynamic<T> input) {
                    return Impl.parseSingleMaterialShorthand(components, input)
                            .mapOrElse(DataResult::success, error -> Codec.list(Codec.PASSTHROUGH)
                                    .parse(input)
                                    .flatMap(unkeyedArguments ->
                                            Impl.parseUnkeyedArguments(components, unkeyedArguments)));
                }
            });
        }
    }

    class Impl {
        static DataResult<RenderProperty> parseSingleMaterialShorthand(
                Map<Construct.Component.Key, RoleReferenceLocation> components, Dynamic<?> input) {
            return VoxFileResourceReference.CODEC.parse(input).flatMap(reference -> {
                if (components.size() != 1) {
                    return DataResult.error(
                            () -> "Single material shorthand used but composition has multiple components");
                }

                if (!components.containsKey(Construct.Component.Key.DEFAULT)) {
                    return DataResult.error(() -> "Single material shorthand used but "
                            + Construct.Component.Key.DEFAULT + " component is missing");
                }

                return DataResult.success(new RenderProperty(
                        List.of(Construct.Component.Key.DEFAULT),
                        Map.of(Construct.Component.Key.DEFAULT, Optional.of(input)),
                        components));
            });
        }

        static DataResult<RenderProperty> parseUnkeyedArguments(
                Map<Construct.Component.Key, RoleReferenceLocation> components, List<Dynamic<?>> unkeyedArguments) {
            List<Construct.Component.Key> keys = new ArrayList<>();
            Map<Construct.Component.Key, Optional<Dynamic<?>>> arguments = new HashMap<>();

            for (Dynamic<?> argument : unkeyedArguments) {
                switch (Argument.parse(argument)) {
                    case DataResult.Success<Argument> success -> {
                        Construct.Component.Key key = success.value().component();
                        Optional<Dynamic<?>> value = success.value().value();

                        if (arguments.containsKey(key)) {
                            return DataResult.error(
                                    () -> "Duplicate component key " + key + " in render property arguments");
                        }

                        if (!components.containsKey(key)) {
                            return DataResult.error(
                                    () -> "Unknown component key " + key + " in render property arguments");
                        }

                        keys.add(key);
                        arguments.put(key, value);
                    }

                    case DataResult.Error<Argument> error -> {
                        return error.map(x -> null);
                    }
                }
            }

            return DataResult.success(new RenderProperty(keys, arguments, components));
        }

        static record Argument(Construct.Component.Key component, Optional<Dynamic<?>> value) {
            public static final Codec<Argument> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                            Construct.Component.Key.CODEC.fieldOf("component").forGetter(Argument::component),
                            Codec.PASSTHROUGH.optionalFieldOf("value").forGetter(Argument::value))
                    .apply(instance, Argument::new));

            public static DataResult<Argument> parse(Dynamic<?> input) {
                return Construct.Component.Key.CODEC
                        .parse(input)
                        .mapOrElse(
                                component -> DataResult.success(new Argument(component, Optional.empty())),
                                error -> Argument.CODEC.parse(input));
            }
        }
    }
}
