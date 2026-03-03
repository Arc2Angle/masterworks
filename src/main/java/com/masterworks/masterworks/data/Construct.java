package com.masterworks.masterworks.data;

import com.masterworks.masterworks.MasterworksDataPackRegistries;
import com.masterworks.masterworks.data.property.Property;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

public record Construct(Holder<Composition> composition, Map<Component.Key, Component> components)
        implements TooltipProvider {

    public static final Codec<Construct> CODEC = Codec.recursive(
            "construct",
            self -> RecordCodecBuilder.create(instance -> instance.group(
                            RegistryFixedCodec.create(MasterworksDataPackRegistries.COMPOSITION)
                                    .fieldOf("composition")
                                    .forGetter(Construct::composition),
                            Codec.unboundedMap(Component.Key.CODEC, Component.CODEC)
                                    .fieldOf("components")
                                    .forGetter(Construct::components))
                    .apply(instance, Construct::new)));

    public static final StreamCodec<RegistryFriendlyByteBuf, Construct> STREAM_CODEC =
            StreamCodec.recursive(self -> StreamCodec.composite(
                    ByteBufCodecs.holderRegistry(MasterworksDataPackRegistries.COMPOSITION),
                    Construct::composition,
                    ByteBufCodecs.map(HashMap::new, Component.Key.STREAM_CODEC, Component.STREAM_CODEC),
                    Construct::components,
                    Construct::new));

    public record Component(Either<Holder<Material>, Construct> value) {
        public static final Codec<Component> CODEC = Codec.either(
                        RegistryFixedCodec.create(MasterworksDataPackRegistries.MATERIAL), Construct.CODEC)
                .xmap(Component::new, Component::value);
        public static final StreamCodec<RegistryFriendlyByteBuf, Component> STREAM_CODEC = ByteBufCodecs.either(
                        ByteBufCodecs.holderRegistry(MasterworksDataPackRegistries.MATERIAL), Construct.STREAM_CODEC)
                .map(Component::new, Component::value);

        public record Key(String value) {
            public static final Codec<Key> CODEC = Codec.STRING.xmap(Key::new, Key::value);
            public static final StreamCodec<ByteBuf, Key> STREAM_CODEC =
                    ByteBufCodecs.STRING_UTF8.map(Key::new, Key::value);

            private MutableComponent format() {
                return net.minecraft.network.chat.Component.literal(
                        " " + Character.toUpperCase(value.charAt(0)) + value.substring(1));
            }
        }

        public Holder<Material> materialOrThrow() {
            return value.left().orElseThrow(() -> new RuntimeException("Component is not a material"));
        }

        public Construct constructOrThrow() {
            return value.right().orElseThrow(() -> new RuntimeException("Component is not a construct"));
        }

        public Property.Container properties() {
            return value.map(material -> material.value().properties(), construct -> construct.properties());
        }

        /**
         * Gets the sub components of this component.
         */
        public Map<Key, Component> components() {
            return value.map(material -> Map.of(), construct -> construct.components);
        }

        private MutableComponent format() {
            return value.map(
                    reference -> {
                        Material material = reference.value();
                        return net.minecraft.network.chat.Component.literal(material.name())
                                .withColor(material.color().argb());
                    },
                    construct -> construct.format());
        }
    }

    public Property.Container properties() {
        return composition.value().properties();
    }

    @Override
    public void addToTooltip(
            Item.TooltipContext context,
            Consumer<net.minecraft.network.chat.Component> adder,
            TooltipFlag flag,
            DataComponentGetter getter) {
        adder.accept(format());
    }

    private MutableComponent format() {
        if (components.size() == 1) {
            return components.values().iterator().next().format();
        }

        return formatWrapBraces(components.entrySet().stream()
                .map(entry -> entry.getValue().format().append(entry.getKey().format()))
                .reduce(Construct::formatJoinPlus)
                .orElseThrow(() -> new RuntimeException("Construct has no components")));
    }

    private static MutableComponent formatWrapBraces(net.minecraft.network.chat.Component value) {
        return net.minecraft.network.chat.Component.literal("(")
                .withColor(0xFFFFFF)
                .append(value)
                .append(net.minecraft.network.chat.Component.literal(")").withColor(0xFFFFFF));
    }

    private static MutableComponent formatJoinPlus(MutableComponent left, MutableComponent right) {
        return left.append(net.minecraft.network.chat.Component.literal(" + ").withColor(0xFFFFFF))
                .append(right);
    }
}
