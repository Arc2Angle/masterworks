package com.masterworks.masterworks.client.renderlet;

import com.masterworks.masterworks.asset.AssetHolder;
import com.masterworks.masterworks.client.MasterworksPreparableReloadListeners;
import com.masterworks.masterworks.client.mesh.Mesh;
import com.masterworks.masterworks.client.submitter.Submitter;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.Material;
import com.mojang.serialization.Codec;
import java.util.Map;

public record MaterialRenderlet(AssetHolder<Mesh> geometry) implements Renderlet {
    public static final Codec<MaterialRenderlet> CODEC =
            AssetHolder.<Mesh>codec().xmap(MaterialRenderlet::new, MaterialRenderlet::geometry);

    public static final Construct.Component.Key COMPONENT_KEY = new Construct.Component.Key("main");

    @Override
    public Submitter build(Map<Construct.Component.Key, Construct.Component> components) {
        if (components.size() != 1) {
            throw new IllegalArgumentException("MaterialRenderer expects exactly one component");
        }

        Construct.Component component = components.get(COMPONENT_KEY);
        if (component == null) {
            throw new IllegalArgumentException("MaterialRenderer expects a component with key 'main'");
        }

        Material material = component
                .material()
                .orElseThrow(() -> new IllegalArgumentException("MaterialRenderer expects a material component"))
                .value();

        return geometry.valueOrThrow(MasterworksPreparableReloadListeners.MESH).reify(material.palette());
    }
}
