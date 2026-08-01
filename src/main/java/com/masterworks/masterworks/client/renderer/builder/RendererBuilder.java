package com.masterworks.masterworks.client.renderer.builder;

import com.masterworks.masterworks.client.part.PartSubmitter;
import com.masterworks.masterworks.data.Construct;
import java.util.Map;

public interface RendererBuilder {
    PartSubmitter build(Map<Construct.Component.Key, Construct.Component> components);
}
