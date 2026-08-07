package com.masterworks.masterworks.client.renderlet;

import com.masterworks.masterworks.client.submitter.CompositeSubmitter;
import com.masterworks.masterworks.client.submitter.Submitter;
import com.masterworks.masterworks.data.Construct;
import com.masterworks.masterworks.data.Role;
import com.mojang.serialization.Codec;
import java.util.Map;

public record CompositeRenderlet(Map<Construct.Component.Key, Role> componentRoles) implements Renderlet {
    public static final Codec<CompositeRenderlet> CODEC = Codec.unboundedMap(Construct.Component.Key.CODEC, Role.CODEC)
            .xmap(CompositeRenderlet::new, CompositeRenderlet::componentRoles);

    @Override
    public Submitter build(Map<Construct.Component.Key, Construct.Component> components) {
        return CompositeSubmitter.of(components.entrySet().stream()
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

                    Renderlet renderer = construct.composition().value().roles().get(role);
                    if (renderer == null) {
                        throw new IllegalArgumentException("Component " + key + " does not provide role: " + role);
                    }

                    return renderer.build(construct.components());
                })
                .toList());
    }
}
