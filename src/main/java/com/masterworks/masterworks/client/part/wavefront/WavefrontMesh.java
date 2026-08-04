package com.masterworks.masterworks.client.part.wavefront;

import com.masterworks.masterworks.client.MasterworksRenderTypes;
import com.masterworks.masterworks.client.palette.Palette;
import com.masterworks.masterworks.client.part.PartSubmitter;
import com.masterworks.masterworks.client.part.submit.CustomGeometryPartSubmitter;
import com.masterworks.masterworks.util.Vertex;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

public record WavefrontMesh(Vertex[] vertices, WavefrontMaterial material) {

    public PartSubmitter colorize(Palette palette) {
        RenderType renderType = MasterworksRenderTypes.PALETTE_ENTITY.apply(
                switch (material) {
                    case WavefrontMaterial.DiffuseTexture(Identifier id) -> id;
                },
                palette.location());

        return new CustomGeometryPartSubmitter(renderType, vertices);
    }
}
