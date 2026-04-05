package com.masterworks.masterworks.client.vox;

import com.masterworks.masterworks.client.etching.Etching;
import com.masterworks.masterworks.util.math.Vector3bc;
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

    public VoxShape clone(Vector3bc mirrors) {
        byte[] resultColorIndices = new byte[colorIndices.length];

        for (int x = 0; x < this.x; x++) {
            for (int y = 0; y < this.y; y++) {
                for (int z = 0; z < this.z; z++) {
                    int src = x + this.x * (y + this.y * z);
                    int dstx = mirrors.x() ? this.x - 1 - x : x;
                    int dsty = mirrors.y() ? this.y - 1 - y : y;
                    int dstz = mirrors.z() ? this.z - 1 - z : z;
                    int dst = dstx + this.x * (dsty + this.y * dstz);
                    resultColorIndices[dst] = colorIndices[src];
                }
            }
        }

        return new VoxShape(x, y, z, resultColorIndices);
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
