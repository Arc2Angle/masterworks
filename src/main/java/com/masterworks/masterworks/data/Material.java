package com.masterworks.masterworks.data;

import com.masterworks.masterworks.client.palette.Palette;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.data.property.util.DecodablePropertyContainer;
import com.masterworks.masterworks.util.Color;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Material(String name, Palette palette, Color color, Property.Container properties) {
    public static final Codec<Material> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("name").forGetter(Material::name),
                    Palette.CODEC.fieldOf("palette").forGetter(Material::palette),
                    Color.ARGB_CODEC.fieldOf("color").forGetter(Material::color),
                    Codec.of(
                                    Encoder.error("Materials cannot be encoded"),
                                    DecodablePropertyContainer.decoder(Property.Dependencies.EMPTY))
                            .fieldOf("properties")
                            .forGetter(Material::properties))
            .apply(instance, Material::new));
}
