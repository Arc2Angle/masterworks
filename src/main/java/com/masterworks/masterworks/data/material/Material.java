package com.masterworks.masterworks.data.material;

import java.util.Map;
import com.masterworks.masterworks.data.Registries;
import com.masterworks.masterworks.data.composition.Stat;
import com.masterworks.masterworks.resource.location.PaletteResourceLocation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Material(String name, PaletteResourceLocation palette, Map<Stat, Double> stats) {

    public static final Codec<Material> CODEC =
            RecordCodecBuilder.create(instance -> instance
                    .group(Codec.STRING.fieldOf("name").forGetter(Material::name),
                            PaletteResourceLocation.CODEC.fieldOf("palette")
                                    .forGetter(Material::palette),
                            Codec.simpleMap(Stat.CODEC, Codec.DOUBLE, Registries.STAT)
                                    .fieldOf("stats").forGetter(Material::stats))
                    .apply(instance, Material::new));

    public boolean hasStat(Stat stat) {
        return stats.containsKey(stat);
    }

    public double getStat(Stat stat) {
        Double value = stats.get(stat);
        if (value == null) {
            throw stat.new IrrelevantException("material: " + name);
        }
        return value;
    }
}
