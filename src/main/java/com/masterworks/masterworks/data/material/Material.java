package com.masterworks.masterworks.data.material;

import com.masterworks.masterworks.Masterworks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record Material(String name, ResourceLocation palette, int durability, float actionSpeed,
        float damage, float armor, float toughness, int enchantability) {

    public static final Codec<Material> CODEC =
            RecordCodecBuilder
                    .create(instance -> instance
                            .group(Codec.STRING.fieldOf("name").forGetter(Material::name),
                                    ResourceLocation.CODEC.fieldOf("palette")
                                            .forGetter(Material::palette),
                                    Codec.INT.fieldOf("durability").forGetter(Material::durability),
                                    Codec.FLOAT.fieldOf("action_speed")
                                            .forGetter(Material::actionSpeed),
                                    Codec.FLOAT.fieldOf("damage").forGetter(Material::damage),
                                    Codec.FLOAT.fieldOf("armor").forGetter(Material::armor),
                                    Codec.FLOAT.fieldOf("toughness").forGetter(Material::toughness),
                                    Codec.INT.fieldOf("enchantability")
                                            .forGetter(Material::enchantability))
                            .apply(instance, Material::new));

    public ResourceLocation getQualifiedPalette() {
        return palette.withPrefix("textures/part/material/").withSuffix(".png");
    }

    public static final Material DEFAULT = new Material("Unknown",
            Masterworks.resourceLocation("default"), 1, 1.0f, 0.0f, 0.0f, 0.0f, 0);
}
