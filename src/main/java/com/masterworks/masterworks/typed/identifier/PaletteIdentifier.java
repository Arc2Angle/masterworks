package com.masterworks.masterworks.typed.identifier;

import com.masterworks.masterworks.util.palette.Palette;
import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;

public record PaletteIdentifier(Identifier id) implements AssetIdentifier<Palette> {
    public static final Codec<PaletteIdentifier> CODEC =
            Identifier.CODEC.xmap(PaletteIdentifier::new, PaletteIdentifier::id);
}
