package com.masterworks.masterworks.client.mesh;

import com.masterworks.masterworks.client.palette.Palette;
import com.masterworks.masterworks.client.submitter.Submitter;
import com.masterworks.masterworks.client.util.ResourcelessReloadListener;
import java.util.HashMap;
import java.util.Map;

public final class MeshReificationCache extends ResourcelessReloadListener {
    private final Map<Key, Submitter> cache = new HashMap<>();

    @Override
    protected void onReload() {
        cache.clear();
    }

    public Submitter get(Mesh mesh, Palette palette) {
        Key key = new Key(mesh, palette);
        Submitter submitter = cache.get(key);
        if (submitter == null) {
            submitter = mesh.reify(palette);
            cache.put(key, submitter);
        }
        return submitter;
    }

    record Key(Mesh mesh, Palette palette) {}
}
