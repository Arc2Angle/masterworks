package com.masterworks.masterworks.client.mesh.core;

import com.masterworks.masterworks.client.mesh.Mesh;
import com.masterworks.masterworks.client.palette.Palette;
import com.masterworks.masterworks.client.submitter.NamespaceSubmitter;
import com.masterworks.masterworks.client.submitter.Submitter;
import java.util.Map;
import java.util.stream.Collectors;

public record NamespaceMesh(Map<String, Mesh> map) implements Mesh {
    @Override
    public Submitter reify(Palette palette) {
        return new NamespaceSubmitter(
                map.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()
                        .reify(palette))));
    }
}
