package com.masterworks.masterworks.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.init.MasterworksDataComponents;
import com.masterworks.masterworks.resource.location.CompositionReferenceResourceLocation;
import com.masterworks.masterworks.resource.location.ItemMaterialReferenceResourceLocation;
import com.masterworks.masterworks.resource.location.MaterialReferenceResourceLocation;
import com.masterworks.masterworks.resource.location.RoleReferenceResourceLocation;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record Construct(CompositionReferenceResourceLocation composition,
        Map<Component.Key, Component> components) {

    public static final Codec<Construct> CODEC =
            Codec.recursive("construct",
                    self -> RecordCodecBuilder.create(instance -> instance
                            .group(CompositionReferenceResourceLocation.CODEC.fieldOf("composition")
                                    .forGetter(Construct::composition),
                                    Codec.unboundedMap(Component.Key.CODEC, Component.CODEC)
                                            .fieldOf("components").forGetter(Construct::components))
                            .apply(instance, Construct::new)));

    public static final StreamCodec<ByteBuf, Construct> STREAM_CODEC = StreamCodec.recursive(
            self -> StreamCodec.composite(CompositionReferenceResourceLocation.STREAM_CODEC,
                    Construct::composition, ByteBufCodecs.map(HashMap::new,
                            Component.Key.STREAM_CODEC, Component.STREAM_CODEC),
                    Construct::components, Construct::new));

    public record Component(Either<MaterialReferenceResourceLocation, Construct> value) {
        public static final Codec<Component> CODEC =
                Codec.either(MaterialReferenceResourceLocation.CODEC, Construct.CODEC)
                        .xmap(Component::new, Component::value);
        public static final StreamCodec<ByteBuf, Component> STREAM_CODEC = ByteBufCodecs
                .either(MaterialReferenceResourceLocation.STREAM_CODEC, Construct.STREAM_CODEC)
                .map(Component::new, Component::value);

        public record Key(String value) {
            public static final Codec<Key> CODEC = Codec.STRING.xmap(Key::new, Key::value);
            public static final StreamCodec<ByteBuf, Key> STREAM_CODEC =
                    ByteBufCodecs.STRING_UTF8.map(Key::new, Key::value);

            public static final Key DEFAULT = new Key("main");
        }

        public static Optional<Component> of(ItemStack stack) {
            if (stack.isEmpty()) {
                return Optional.empty();
            }

            Construct construct = stack.get(MasterworksDataComponents.CONSTRUCT);
            if (construct != null) {
                return Optional.of(new Component(Either.right(construct)));
            }

            return Optional.ofNullable(BuiltInRegistries.ITEM.getKeyOrNull(stack.getItem()))
                    .flatMap(item -> new ItemMaterialReferenceResourceLocation(item).dataMapped())
                    .map(material -> new Component(Either.left(material)));
        }

        public Stream<RoleReferenceResourceLocation> roles() {
            return value.map(material -> Stream.of(RoleReferenceResourceLocation.MATERIAL),
                    construct -> construct.composition.registered().value().properties().keySet()
                            .stream());
        }
    }

    /**
     * A helper which gets the render property for the given role from this construct's composition.
     * 
     * @throws PropertyAccessException if the role or property is missing
     */
    public <T extends Property> T getPropertyOrThrow(Property.Type<T> type,
            RoleReferenceResourceLocation role) {
        return Optional.ofNullable(composition.registered().value().properties().get(role))
                .orElseThrow(() -> new PropertyAccessException(
                        "Construct composition " + composition + " missing " + role + " role"))
                .get(type).orElseThrow(() -> new PropertyAccessException("Construct composition "
                        + composition + " missing " + type + " property in " + role + " role"));
    }

    public static class PropertyAccessException extends RuntimeException {
        public PropertyAccessException(String message) {
            super(message);
        }
    }
}
