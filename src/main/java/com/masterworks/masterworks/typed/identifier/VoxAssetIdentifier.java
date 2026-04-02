package com.masterworks.masterworks.typed.identifier;

import com.masterworks.masterworks.client.vox.VoxAsset;
import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;

public record VoxAssetIdentifier(Identifier id) implements AssetIdentifier<VoxAsset> {
    public static final Codec<VoxAssetIdentifier> CODEC =
            Identifier.CODEC.xmap(VoxAssetIdentifier::new, VoxAssetIdentifier::id);
}
