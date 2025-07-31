package com.masterworks.masterworks.data.construct;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import com.masterworks.masterworks.Masterworks;
import com.masterworks.masterworks.data.Maps;
import com.masterworks.masterworks.data.composition.Composition;
import com.masterworks.masterworks.data.composition.Stat;
import com.masterworks.masterworks.data.material.Material;
import com.masterworks.masterworks.util.Expression;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

/**
 * Represents a dynamic tool's composition.
 */
public record Construct(ResourceLocation template, int variant,
        List<Either<ResourceLocation, Construct>> parts) {
    public static void init() {}

    private static final int MAX_PARTS = 5;

    public static final Codec<Construct> CODEC =
            Codec.recursive("construct",
                    self -> RecordCodecBuilder
                            .create(instance -> instance.group(
                                    ResourceLocation.CODEC.fieldOf("template")
                                            .forGetter(Construct::template),
                                    Codec.INT.fieldOf("variant").forGetter(Construct::variant),
                                    Codec.list(Codec.either(ResourceLocation.CODEC, self))
                                            .fieldOf("parts").forGetter(Construct::parts))
                                    .apply(instance, Construct::new)));

    public static final StreamCodec<ByteBuf, Construct> STREAM_CODEC =
            StreamCodec
                    .recursive(self -> StreamCodec.composite(ResourceLocation.STREAM_CODEC,
                            Construct::template, ByteBufCodecs.INT, Construct::variant,
                            ByteBufCodecs.either(ResourceLocation.STREAM_CODEC, self)
                                    .apply(ByteBufCodecs.list(MAX_PARTS)),
                            Construct::parts, Construct::new));

    public static final Supplier<DataComponentType<Construct>> DATA_COMPONENT =
            Masterworks.DATA_COMPONENTS.registerComponentType("construct", builder -> builder
                    .networkSynchronized(Construct.STREAM_CODEC).persistent(Construct.CODEC));

    public static List<Composition> getCompositionsByTemplateItem(ResourceLocation templateItem) {
        Holder.Reference<Item> templateItemHolder =
                BuiltInRegistries.ITEM.get(templateItem).orElse(null);

        if (templateItemHolder == null) {
            throw new IllegalArgumentException(
                    "Construct template item not found: " + templateItem);
        }

        List<Composition> compositions = templateItemHolder.getData(Maps.COMPOSITIONS);

        if (compositions == null) {
            throw new IllegalArgumentException(
                    "Construct template data missing for item: " + templateItem);
        }

        return compositions;
    }

    public ResourceLocation getOnlyMaterialItem() {
        if (parts.size() != 1) {
            throw new IllegalStateException("Construct with multiple parts: " + this);
        }

        return parts.get(0).left()
                .orElseThrow(() -> new IllegalStateException("Complex single part: " + this));
    }

    public ResourceLocation getQualifiedTemplateItem() {
        return template.withPrefix("template/");
    }

    public Composition getComposition() {
        try {
            return getCompositionsByTemplateItem(getQualifiedTemplateItem()).get(variant);
        } catch (IndexOutOfBoundsException e) {
            throw new UnknownVariantException();
        }
    }

    public boolean hasStat(Stat stat) {
        return getComposition().properties().containsKey(stat);
    }

    public double getStat(Stat stat) {
        Composition composition = getComposition();
        Expression expression = composition.properties().get(stat);

        if (expression == null) {
            throw stat.new IrrelevantException("construct template: " + template);
        }

        List<Double> arguments = parts.stream()
                .map(part -> part.map(materialItem -> Material.fromItem(materialItem).getStat(stat),
                        subConstruct -> subConstruct.getStatOrNull(stat)))
                .collect(Collectors.toList());

        return expression.evaluate(arguments);
    }

    private Double getStatOrNull(Stat stat) {
        try {
            return getStat(stat);
        } catch (Stat.IrrelevantException e) {
            return null;
        }
    }

    public class UnknownVariantException extends RuntimeException {
        public UnknownVariantException() {
            super("Unknown variant identifier: " + variant + " for construct template: "
                    + template);
        }
    }
}
