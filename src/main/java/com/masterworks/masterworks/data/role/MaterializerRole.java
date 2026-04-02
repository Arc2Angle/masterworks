package com.masterworks.masterworks.data.role;

import com.masterworks.masterworks.MasterworksRoleTypes;
import com.masterworks.masterworks.client.MasterworksReloadListeners;
import com.masterworks.masterworks.client.asset.manager.PaletteManager;
import com.masterworks.masterworks.client.asset.manager.VoxModelManager;
import com.masterworks.masterworks.client.palette.Palette;
import com.masterworks.masterworks.client.vox.VoxAsset;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.typed.identifier.VoxAssetIdentifier;
import com.masterworks.masterworks.util.codec.FlatMapCodec;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public record MaterializerRole(Map<Construct.Component.Key, VoxAssetIdentifier> arguments) implements Role {
    public static final MapCodec<MaterializerRole> CODEC = FlatMapCodec.forDispatchCodec(
                    Construct.Component.Key.CODEC, VoxAssetIdentifier.CODEC, new Construct.Component.Key("type"))
            .xmap(MaterializerRole::new, MaterializerRole::arguments);

    @Override
    public Type<?> type() {
        return MasterworksRoleTypes.MATERIALIZER.get();
    }

    @Override
    public Stream<VoxAsset> render(Map<Construct.Component.Key, Construct.Component> components) {
        VoxModelManager voxModelManager = MasterworksReloadListeners.VOX_MODEL_MANAGER.get();
        PaletteManager paletteManager = MasterworksReloadListeners.PALETTE_MANAGER.get();

        return components.entrySet().stream().map(entry -> {
            Construct.Component.Key key = entry.getKey();
            Construct.Component component = entry.getValue();

            VoxAsset voxAsset = Optional.ofNullable(arguments.get(key))
                    .orElseThrow(() -> new RuntimeException("Missing key: " + key))
                    .assetOrThrow(voxModelManager);
            Palette palette = component.materialOrThrow().value().palette().assetOrThrow(paletteManager);

            return new VoxAsset(voxAsset.model(), palette);
        });
    }
}
