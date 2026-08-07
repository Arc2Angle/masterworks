package com.masterworks.masterworks.client.mesh.core;

import com.masterworks.masterworks.client.MasterworksRenderTypes;
import com.masterworks.masterworks.client.mesh.Mesh;
import com.masterworks.masterworks.client.palette.Palette;
import com.masterworks.masterworks.client.submitter.CustomGeometrySubmitter;
import com.masterworks.masterworks.client.submitter.Submitter;
import com.masterworks.masterworks.util.Vertex;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

public record DiffuseMesh(Identifier diffuseLocation, Vertex[] vertices) implements Mesh {
    @Override
    public Submitter reify(Palette palette) {
        RenderType renderType = MasterworksRenderTypes.PALETTE_ENTITY.apply(diffuseLocation, palette.location());
        return new CustomGeometrySubmitter(renderType, vertices);
    }
}
