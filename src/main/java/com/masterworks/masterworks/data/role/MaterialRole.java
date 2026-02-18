package com.masterworks.masterworks.data.role;

import com.masterworks.masterworks.MasterworksPreparableReloadListeners;
import com.masterworks.masterworks.MasterworksRoleTypes;
import com.masterworks.masterworks.client.resource.manager.PaletteManager;
import com.masterworks.masterworks.client.resource.manager.VoxFileManager;
import com.masterworks.masterworks.client.resource.reference.PaletteResourceReference;
import com.masterworks.masterworks.client.resource.reference.VoxFileResourceReference;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.location.RoleReferenceLocation;
import com.masterworks.masterworks.location.ShapeReferenceLocation;
import com.masterworks.masterworks.util.palette.Palette;
import com.masterworks.masterworks.util.vox.VoxFile;
import com.masterworks.masterworks.util.vox.Voxels;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public record MaterialRole(List<ShapeReferenceLocation> examples) implements Role {
    public static final MapCodec<MaterialRole> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.list(ShapeReferenceLocation.CODEC).fieldOf("examples").forGetter(MaterialRole::examples))
            .apply(instance, MaterialRole::new));

    @Override
    public Type<?> type() {
        return MasterworksRoleTypes.MATERIAL.get();
    }

    @Override
    public Stream<Voxels> render(
            RoleReferenceLocation reference, Construct.Component component, Optional<Dynamic<?>> argument) {
        PaletteManager paletteManager = MasterworksPreparableReloadListeners.PALETTE_MANAGER.get();
        VoxFileManager voxFileManager = MasterworksPreparableReloadListeners.VOX_FILE_MANAGER.get();

        PaletteResourceReference paletteReference = component
                .value()
                .mapRight(construct ->
                        new IllegalStateException("MaterialRole cannot render a construct component: " + component))
                .orThrow()
                .registered()
                .value()
                .palette();

        VoxFileResourceReference voxFileReference = VoxFileResourceReference.CODEC
                .parse(argument.orElseThrow(() ->
                        new IllegalStateException("Material rendering requires a vox file reference: " + argument)))
                .getOrThrow(message -> new IllegalStateException("Failed to parse argument: " + message));

        Palette palette = paletteManager.getOrThrow(paletteReference);
        VoxFile voxFile = voxFileManager.getOrThrow(voxFileReference);

        return Stream.of(voxFile.get(palette));
    }
}
