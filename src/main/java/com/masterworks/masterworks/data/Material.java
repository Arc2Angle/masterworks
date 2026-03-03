package com.masterworks.masterworks.data;

import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.typed.identifier.PaletteIdentifier;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;

public record Material(String name, PaletteIdentifier palette, Color color, Property.Container properties) {
    public static final Codec<Material> CODEC =
            Codec.lazyInitialized(() -> RecordCodecBuilder.create(instance -> instance.group(
                            Codec.STRING.fieldOf("name").forGetter(Material::name),
                            PaletteIdentifier.CODEC.fieldOf("palette").forGetter(Material::palette),
                            Color.CODEC.fieldOf("color").forGetter(Material::color),
                            Property.Container.basicCodec(Set.of())
                                    .fieldOf("properties")
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
