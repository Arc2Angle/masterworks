package com.masterworks.masterworks.data;

import com.masterworks.masterworks.asset.AssetHolder;
import com.masterworks.masterworks.client.palette.Palette;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.data.property.util.DecodablePropertyContainer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Material(String name, AssetHolder<Palette> palette, Color color, Property.Container properties) {
    public static final Codec<Material> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("name").forGetter(Material::name),
                    AssetHolder.<Palette>codec().fieldOf("palette").forGetter(Material::palette),
                    Color.CODEC.fieldOf("color").forGetter(Material::color),
                    Codec.of(
                                    Encoder.error("Materials cannot be encoded"),
                                    DecodablePropertyContainer.decoder(Property.Dependencies.EMPTY))
                            .fieldOf("properties")
                            .forGetter(Material::properties))
            .apply(instance, Material::new));

    public record Color(int argb) {
        public static final Codec<Color> CODEC = Codec.STRING.xmap(Color::parse, Color::format);

        public static Color parse(String value) {
            return new Color((int) Long.parseLong(value, 16));
        }

        public String format() {
            return String.format("%08X", argb);
        }
    }
}
