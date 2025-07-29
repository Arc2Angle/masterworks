package com.masterworks.masterworks.data.material;

import java.util.Map;
import com.masterworks.masterworks.Masterworks;
import com.masterworks.masterworks.data.Maps;
import com.masterworks.masterworks.data.Registries;
import com.masterworks.masterworks.data.composition.Stat;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public record Material(String name, ResourceLocation palette, Map<Stat, Double> stats) {

    public static final Codec<Material> CODEC =
            RecordCodecBuilder.create(instance -> instance
                    .group(Codec.STRING.fieldOf("name").forGetter(Material::name),
                            ResourceLocation.CODEC.fieldOf("palette").forGetter(Material::palette),
                            Codec.simpleMap(Stat.CODEC, Codec.DOUBLE, Registries.STAT)
                                    .fieldOf("stats").forGetter(Material::stats))
                    .apply(instance, Material::new));

    public ResourceLocation getQualifiedPalette() {
        return palette.withPrefix("textures/part/material/").withSuffix(".png");
    }

    public static final Material DEFAULT =
            new Material("Unknown", Masterworks.resourceLocation("default"),
                    Map.of(Stat.DURABILITY, 1.0, Stat.DAMAGE, 0.0, Stat.ACTION_SPEED, 0.0,
                            Stat.ARMOR, 0.0, Stat.TOUGHNESS, 0.0, Stat.ENCHANTABILITY, 0.0));

    public static Material getMaterialByItem(ResourceLocation materialItem) {
        Holder.Reference<Item> item = BuiltInRegistries.ITEM.get(materialItem).orElse(null);

        if (item == null) {
            throw new IllegalArgumentException("Material item not found: " + materialItem);
        }

        Material material = item.getData(Maps.MATERIALS);

        if (material == null) {
            throw new IllegalArgumentException("Material not found for item: " + materialItem);
        }

        return material;
    }

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
