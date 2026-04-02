package com.masterworks.masterworks.client.vox;

import com.masterworks.masterworks.client.etching.Etching;
import java.util.Arrays;
import org.joml.Vector3i;
import org.joml.Vector3ic;

public record VoxShape(int x, int y, int z, byte[] colorIndices) implements Cloneable {
    public Vector3ic count() {
        return new Vector3i(x, y, z);
    }

    @Override
    public VoxShape clone() {
        return new VoxShape(x, y, z, Arrays.copyOf(colorIndices, colorIndices.length));
    }

    public void etched(Etching etching, int etchingZ, byte colorIndex) {
        if (x != etching.x() || y != etching.y()) {
            throw new IllegalArgumentException("Cannot etch shape with different x or y counts: " + count() + " vs "
                    + etching.x() + "x" + etching.y());
        }

        int xy = x * y;
        boolean[] map = etching.values();

        for (int i = 0; i < xy; i++) {
            if (!map[i]) {
                continue;
            }

            colorIndices[i + etchingZ * xy] = colorIndex;
        }
    }
}
