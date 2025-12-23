package com.masterworks.masterworks.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import com.masterworks.masterworks.MasterworksDataComponents;
import com.masterworks.masterworks.data.property.PropertyContainer;
import com.masterworks.masterworks.location.CompositionReferenceLocation;
import com.masterworks.masterworks.location.ItemMaterialReferenceLocation;
import com.masterworks.masterworks.location.MaterialReferenceLocation;
import com.masterworks.masterworks.location.RoleReferenceLocation;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record Construct(CompositionReferenceLocation composition,
        Map<Component.Key, Component> components) {

    public static final Codec<Construct> CODEC =
            Codec.recursive("construct",
                    self -> RecordCodecBuilder.create(instance -> instance
                            .group(CompositionReferenceLocation.CODEC.fieldOf("composition")
                                    .forGetter(Construct::composition),
                                    Codec.unboundedMap(Component.Key.CODEC, Component.CODEC)
                                            .fieldOf("components").forGetter(Construct::components))
                            .apply(instance, Construct::new)));

    public static final StreamCodec<ByteBuf, Construct> STREAM_CODEC = StreamCodec
            .recursive(self -> StreamCodec.composite(CompositionReferenceLocation.STREAM_CODEC,
                    Construct::composition, ByteBufCodecs.map(HashMap::new,
                            Component.Key.STREAM_CODEC, Component.STREAM_CODEC),
                    Construct::components, Construct::new));

    public record Component(Either<MaterialReferenceLocation, Construct> value) {
        public static final Codec<Component> CODEC =
                Codec.either(MaterialReferenceLocation.CODEC, Construct.CODEC).xmap(Component::new,
                        Component::value);
        public static final StreamCodec<ByteBuf, Component> STREAM_CODEC =
                ByteBufCodecs.either(MaterialReferenceLocation.STREAM_CODEC, Construct.STREAM_CODEC)
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
                    .flatMap(item -> new ItemMaterialReferenceLocation(item).dataMapped())
                    .map(material -> new Component(Either.left(material)));
        }

        public Stream<RoleReferenceLocation> roles() {
            return value.map(material -> Stream.of(RoleReferenceLocation.MATERIAL),
                    construct -> construct.composition.registered().value().properties().keySet()
                            .stream());
            // TODO: just return a set
        }

        /**
         * Gets the sub components of this component.
         */
        public Map<Key, Component> components() {
            return value.map(material -> Map.of(), construct -> construct.components);
        }

        /**
         * Gets the properties for the given role from this construct's composition.
         * 
         * @throws RuntimeException if the role is missing
         */
        public PropertyContainer properties(RoleReferenceLocation role) {
            return value.map(material -> {
                if (!role.equals(RoleReferenceLocation.MATERIAL)) {
                    throw new RuntimeException("Material component does not have role " + role);
                }

                return material.registered().value().properties();
            }, construct -> {
                return construct.properties(role);
            });
        }
    }

    /**
     * Gets the properties for the given role from this construct's composition.
     * 
     * @throws RuntimeException if the role is missing
     */
    public PropertyContainer properties(RoleReferenceLocation role) {
        PropertyContainer roleProperties = composition.registered().value().properties().get(role);

        if (roleProperties == null) {
            throw new RuntimeException(
                    "Construct composition " + composition + " missing " + role + " role");
        }

        return roleProperties;
    }
}
