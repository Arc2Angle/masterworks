package com.masterworks.masterworks.data.construct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import com.masterworks.masterworks.data.Maps;
import com.masterworks.masterworks.data.composition.Composition;
import com.masterworks.masterworks.data.composition.Part;
import com.masterworks.masterworks.data.composition.Stat;
import com.masterworks.masterworks.util.Expression;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

/**
 * Represents a dynamic tool's composition.
 */
public record Construct(ResourceLocation template, Map<String, Construct> parts) {

    public static final Codec<Construct> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(ResourceLocation.CODEC.fieldOf("template").forGetter(Construct::template),
                    Codec.dispatchedMap(Codec.STRING, key -> Construct.CODEC).fieldOf("parts")
                            .forGetter(Construct::parts))
            .apply(instance, Construct::new));

    // might require Codec.recursive if Codec.dispatchedMap doesn't lazy load

    public static final StreamCodec<ByteBuf, Construct> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, Construct::template,
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, Construct.STREAM_CODEC, 256),
            Construct::parts, Construct::new);


    // might require StreamCodec.recursive if ByteBufCodecs.map doesn't lazy load

    public static List<Composition> getAllCompositions(ResourceLocation template) {
        Holder.Reference<Item> templateItem = BuiltInRegistries.ITEM.get(template)
                .orElseThrow(() -> new IllegalStateException("Construct template item not found"));

        List<Composition> compositions =
                Optional.ofNullable(templateItem.getData(Maps.COMPOSITIONS)).orElseThrow(
                        () -> new IllegalStateException("Compositions missing for template"));

        return compositions;
    }

    public Composition getComposition() {
        Set<String> constructPartNames = parts.keySet();

        for (Composition composition : getAllCompositions(template)) {
            Set<String> compositionPartNames =
                    composition.parts().stream().map(Part::name).collect(Collectors.toSet());

            if (compositionPartNames.equals(constructPartNames)) {
                return composition;
            }
        }

        throw new UnknownPartSetException(constructPartNames);
    }

    public boolean relevant(Stat stat) {
        return getComposition().properties().containsKey(stat.name);
    }

    public double getStat(Stat stat) {
        Composition composition = getComposition();
        Expression expression = composition.properties().get(stat.name);

        if (expression == null) {
            throw new IrrelevantStatException(stat);
        }

        Set<String> parameters = expression.parameters().collect(Collectors.toSet());
        Map<String, Double> arguments = new HashMap<>();

        for (Map.Entry<String, Construct> partEntry : parts.entrySet()) {
            if (parameters.contains(partEntry.getKey())) {
                arguments.put(partEntry.getKey(), partEntry.getValue().getStat(stat));
            }
        }

        return expression.evaluate(arguments);
    }

    public class UnknownPartSetException extends RuntimeException {
        public UnknownPartSetException(Set<String> partNames) {
            super("Unknown part set: " + partNames + " for construct template: " + template);
        }
    }

    public class IrrelevantStatException extends RuntimeException {
        public IrrelevantStatException(Stat stat) {
            super("Stat " + stat.name + " is not relevant for construct template: " + template);
        }
    }
}
