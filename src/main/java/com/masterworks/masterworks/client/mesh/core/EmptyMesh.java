package com.masterworks.masterworks.client.mesh.core;

import com.masterworks.masterworks.client.mesh.Mesh;
import com.masterworks.masterworks.client.palette.Palette;
import com.masterworks.masterworks.client.submitter.EmptySubmitter;
import com.masterworks.masterworks.client.submitter.Submitter;

public record EmptyMesh() implements Mesh {
    @Override
    public Submitter reify(Palette palette) {
        return new EmptySubmitter();
    }
}
