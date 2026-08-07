package com.masterworks.masterworks.client.mesh;

import com.masterworks.masterworks.client.palette.Palette;
import com.masterworks.masterworks.client.submitter.Submitter;

public interface Mesh {
    Submitter reify(Palette palette);
}
