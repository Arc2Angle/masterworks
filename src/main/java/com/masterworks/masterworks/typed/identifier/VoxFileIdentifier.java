package com.masterworks.masterworks.typed.identifier;

import com.masterworks.masterworks.util.vox.VoxFile;
import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;

public record VoxFileIdentifier(Identifier id) implements AssetIdentifier<VoxFile> {
    public static final Codec<VoxFileIdentifier> CODEC =
            Identifier.CODEC.xmap(VoxFileIdentifier::new, VoxFileIdentifier::id);
}
