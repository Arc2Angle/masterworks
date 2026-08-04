package com.masterworks.masterworks.client.renderer.builder;

import com.masterworks.masterworks.asset.AssetHolder;
import com.masterworks.masterworks.client.MasterworksPreparableReloadListeners;
import com.masterworks.masterworks.client.part.PartGeometry;
import com.masterworks.masterworks.client.part.PartSubmitter;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.Material;
import com.mojang.serialization.Codec;
import java.util.Map;

public record MaterialRendererBuilder(AssetHolder<PartGeometry> geometry) implements RendererBuilder {
    public static final Codec<MaterialRendererBuilder> CODEC =
            AssetHolder.<PartGeometry>codec().xmap(MaterialRendererBuilder::new, MaterialRendererBuilder::geometry);

    public static final Construct.Component.Key COMPONENT_KEY = new Construct.Component.Key("main");

    @Override
    public PartSubmitter build(Map<Construct.Component.Key, Construct.Component> components) {
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

        return geometry.valueOrThrow(MasterworksPreparableReloadListeners.PART).colorize(material.palette());
    }
}
