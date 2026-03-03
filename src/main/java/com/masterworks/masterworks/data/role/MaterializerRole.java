package com.masterworks.masterworks.data.role;

import com.masterworks.masterworks.MasterworksPreparableReloadListeners;
import com.masterworks.masterworks.MasterworksRoleTypes;
import com.masterworks.masterworks.client.resource.manager.PaletteManager;
import com.masterworks.masterworks.client.resource.manager.VoxFileManager;
import com.masterworks.masterworks.client.resource.reference.VoxFileResourceReference;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.property.Property;
import com.masterworks.masterworks.util.codec.FlatMapCodec;
import com.masterworks.masterworks.util.palette.Palette;
import com.masterworks.masterworks.util.vox.VoxFile;
import com.masterworks.masterworks.util.vox.Voxels;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public record MaterializerRole(Map<Construct.Component.Key, VoxFileResourceReference> arguments) implements Role {
    public static final MapCodec<MaterializerRole> CODEC = FlatMapCodec.forDispatchCodec(
                    Construct.Component.Key.CODEC, VoxFileResourceReference.CODEC, new Construct.Component.Key("type"))
            .xmap(MaterializerRole::new, MaterializerRole::arguments);

    @Override
    public Type<?> type() {
        return MasterworksRoleTypes.MATERIALIZER.get();
    }

    @Override
    public Property.Container properties(Construct construct, Construct.Component.Key key) {
        return construct.components().get(key).materialOrThrow().properties();
    }

    @Override
    public Stream<Voxels> render(Map<Construct.Component.Key, Construct.Component> components) {
        VoxFileManager voxFileManager = MasterworksPreparableReloadListeners.VOX_FILE_MANAGER.get();
        PaletteManager paletteManager = MasterworksPreparableReloadListeners.PALETTE_MANAGER.get();

        return components.entrySet().stream().map(entry -> {
            Construct.Component.Key key = entry.getKey();
            Construct.Component component = entry.getValue();

            VoxFile voxFile = voxFileManager.getOrThrow(Optional.ofNullable(arguments.get(key))
                    .orElseThrow(() -> new RuntimeException("Missing key: " + key)));
            Palette palette =
                    paletteManager.getOrThrow(component.materialOrThrow().palette());

            return voxFile.voxels(palette);
        });
    }
}
