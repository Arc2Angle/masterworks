package com.masterworks.masterworks.properties.tool.part.material;

import com.masterworks.masterworks.Masterworks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record ToolPartMaterialProperties(String name, ResourceLocation palate, int durability,
        float actionSpeed, float damage, float armor, float toughness, int enchantability) {

    public static final Codec<ToolPartMaterialProperties> CODEC =
            RecordCodecBuilder.create(instance -> instance
                    .group(Codec.STRING.fieldOf("name").forGetter(ToolPartMaterialProperties::name),
                            ResourceLocation.CODEC.fieldOf("palate")
                                    .forGetter(ToolPartMaterialProperties::palate),
                            Codec.INT
                                    .fieldOf("durability")
                                    .forGetter(ToolPartMaterialProperties::durability),
                            Codec.FLOAT.fieldOf("action_speed")
                                    .forGetter(ToolPartMaterialProperties::actionSpeed),
                            Codec.FLOAT.fieldOf("damage")
                                    .forGetter(ToolPartMaterialProperties::damage),
                            Codec.FLOAT.fieldOf("armor")
                                    .forGetter(ToolPartMaterialProperties::armor),
                            Codec.FLOAT.fieldOf("toughness")
                                    .forGetter(ToolPartMaterialProperties::toughness),
                            Codec.INT.fieldOf("enchantability")
                                    .forGetter(ToolPartMaterialProperties::enchantability))
                    .apply(instance, ToolPartMaterialProperties::new));

    public static final ToolPartMaterialProperties DEFAULT = new ToolPartMaterialProperties(
            "Unknown", ResourceLocation.fromNamespaceAndPath(Masterworks.MOD_ID, "default"), 1,
            1.0f, 0.0f, 0.0f, 0.0f, 0);
}
