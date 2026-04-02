package com.masterworks.masterworks.typed.identifier;

import com.masterworks.masterworks.client.etching.Etching;
import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;

public record EtchingIdentifier(Identifier id) implements AssetIdentifier<Etching> {
    public static final Codec<EtchingIdentifier> CODEC =
            Identifier.CODEC.xmap(EtchingIdentifier::new, EtchingIdentifier::id);
}
