package com.masterworks.masterworks.data.construct;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import com.masterworks.masterworks.Masterworks;
import com.masterworks.masterworks.data.composition.Composition;
import com.masterworks.masterworks.data.composition.Stat;
import com.masterworks.masterworks.resource.location.TemplateResourceLocation;
import com.masterworks.masterworks.resource.location.MaterialResourceLocation;
import com.masterworks.masterworks.util.Expression;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Represents a dynamic tool's composition.
 */
public record Construct(TemplateResourceLocation template, int variant,
        List<Either<MaterialResourceLocation, Construct>> parts) {
    public static void init() {}

    private static final int MAX_PARTS = 5;

    public static final Codec<Construct> CODEC =
            Codec.recursive("construct",
                    self -> RecordCodecBuilder.create(instance -> instance
                            .group(TemplateResourceLocation.CODEC.fieldOf("template")
                                    .forGetter(Construct::template),
                                    Codec.INT.fieldOf("variant").forGetter(Construct::variant),
                                    Codec.list(Codec.either(MaterialResourceLocation.CODEC, self))
                                            .fieldOf("parts").forGetter(Construct::parts))
                            .apply(instance, Construct::new)));

    public static final StreamCodec<ByteBuf, Construct> STREAM_CODEC =
            StreamCodec
                    .recursive(self -> StreamCodec.composite(TemplateResourceLocation.STREAM_CODEC,
                            Construct::template, ByteBufCodecs.INT, Construct::variant,
                            ByteBufCodecs.either(MaterialResourceLocation.STREAM_CODEC, self)
                                    .apply(ByteBufCodecs.list(MAX_PARTS)),
                            Construct::parts, Construct::new));

    public static final Supplier<DataComponentType<Construct>> DATA_COMPONENT =
            Masterworks.DATA_COMPONENTS.registerComponentType("construct", builder -> builder
                    .networkSynchronized(Construct.STREAM_CODEC).persistent(Construct.CODEC));

    public Optional<MaterialResourceLocation> getMaterialIfSingle() {
        if (parts.size() != 1) {
            return Optional.empty();
        }

        return parts.get(0).left();
    }

    public Composition getComposition() {
        try {
            return template.getMappedValue().get(variant);
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

        List<Double> arguments =
                parts.stream()
                        .map(part -> part.map(material -> material.getMappedValue().getStat(stat),
                                construct -> construct.getStatOrNull(stat)))
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
