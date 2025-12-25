package com.masterworks.masterworks.data;

import java.util.Map;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.location.PaletteReferenceLocation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Material(String name, PaletteReferenceLocation palette, Color color,
        Property.Container properties) {
    public static final Codec<Material> CODEC =
            Codec.lazyInitialized(() -> RecordCodecBuilder.create(instance -> instance
                    .group(Codec.STRING.fieldOf("name").forGetter(Material::name),
                            PaletteReferenceLocation.CODEC.fieldOf("palette")
                                    .forGetter(Material::palette),
                            Color.CODEC.fieldOf("color").forGetter(Material::color),
                            Property.Container.basicCodec(Map.of()).fieldOf("properties")
                                    .forGetter(Material::properties))
                    .apply(instance, Material::new)));

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
