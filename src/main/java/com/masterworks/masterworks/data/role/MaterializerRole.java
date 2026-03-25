package com.masterworks.masterworks.data.role;

import com.masterworks.masterworks.MasterworksRoleTypes;
import com.masterworks.masterworks.client.MasterworksReloadListeners;
import com.masterworks.masterworks.client.asset.manager.PaletteManager;
import com.masterworks.masterworks.client.asset.manager.VoxFileManager;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.typed.identifier.VoxFileIdentifier;
import com.masterworks.masterworks.util.codec.FlatMapCodec;
import com.masterworks.masterworks.util.palette.Palette;
import com.masterworks.masterworks.util.vox.VoxFile;
import com.masterworks.masterworks.util.vox.Voxels;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public record MaterializerRole(Map<Construct.Component.Key, VoxFileIdentifier> arguments) implements Role {
    public static final MapCodec<MaterializerRole> CODEC = FlatMapCodec.forDispatchCodec(
                    Construct.Component.Key.CODEC, VoxFileIdentifier.CODEC, new Construct.Component.Key("type"))
            .xmap(MaterializerRole::new, MaterializerRole::arguments);

    @Override
    public Type<?> type() {
        return MasterworksRoleTypes.MATERIALIZER.get();
    }

    @Override
    public Stream<Voxels> render(Map<Construct.Component.Key, Construct.Component> components) {
        VoxFileManager voxFileManager = MasterworksReloadListeners.VOX_FILE_MANAGER.get();
        PaletteManager paletteManager = MasterworksReloadListeners.PALETTE_MANAGER.get();

        return components.entrySet().stream().map(entry -> {
            Construct.Component.Key key = entry.getKey();
            Construct.Component component = entry.getValue();

            VoxFile voxFile = Optional.ofNullable(arguments.get(key))
                    .orElseThrow(() -> new RuntimeException("Missing key: " + key))
                    .assetOrThrow(voxFileManager);
            Palette palette = component.materialOrThrow().value().palette().assetOrThrow(paletteManager);

            return voxFile.voxels(palette);
        });
    }
}
