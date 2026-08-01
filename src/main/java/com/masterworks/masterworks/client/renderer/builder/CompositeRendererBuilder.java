package com.masterworks.masterworks.client.renderer.builder;

import com.masterworks.masterworks.client.part.PartSubmitter;
import com.masterworks.masterworks.client.part.submit.CompositePartSubmitter;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.Role;
import com.mojang.serialization.Codec;
import java.util.Map;

public record CompositeRendererBuilder(Map<Construct.Component.Key, Role> componentRoles) implements RendererBuilder {
    public static final Codec<CompositeRendererBuilder> CODEC = Codec.unboundedMap(
                    Construct.Component.Key.CODEC, Role.CODEC)
            .xmap(CompositeRendererBuilder::new, CompositeRendererBuilder::componentRoles);

    @Override
    public PartSubmitter build(Map<Construct.Component.Key, Construct.Component> components) {
        return new CompositePartSubmitter(components.entrySet().stream()
                .map(entry -> {
                    Construct.Component.Key key = entry.getKey();
                    Construct construct = entry.getValue()
                            .construct()
                            .orElseThrow(() ->
                                    new IllegalArgumentException("CompositeRenderer expects a construct component"));

                    Role role = componentRoles.get(key);
                    if (role == null) {
                        throw new IllegalArgumentException("CompositeRenderer missing role for component: " + key);
                    }

                    RendererBuilder renderer =
                            construct.composition().value().roles().get(role);
                    if (renderer == null) {
                        throw new IllegalArgumentException("Component " + key + " does not provide role: " + role);
                    }

                    return renderer.build(construct.components());
                })
                .toList());
    }
}
