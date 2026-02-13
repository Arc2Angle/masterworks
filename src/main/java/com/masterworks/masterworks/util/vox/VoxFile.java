package com.masterworks.masterworks.util.vox;

import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class VoxFile {
    public final int version;
    private final List<VoxChunk> chunks;

    VoxFile(int version, List<VoxChunk> chunks) {
        this.version = version;
        this.chunks = chunks;
    }

    public static VoxFile read(InputStream stream) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(stream.readAllBytes()).order(ByteOrder.LITTLE_ENDIAN);

        byte[] header = new byte[4];
        try {
            buffer.get(header);
        } catch (BufferUnderflowException e) {
            throw new RuntimeException("Missing VOX file header", e);
        }

        if (header[0] != 'V' || header[1] != 'O' || header[2] != 'X' || header[3] != ' ') {
            throw new RuntimeException("Invalid VOX file header");
        }

        int version;
        try {
            version = buffer.getInt();
        } catch (BufferUnderflowException e) {
            throw new RuntimeException("Missing VOX file version", e);
        }

        List<VoxChunk> chunks = new ArrayList<>();

        try {
            while (buffer.hasRemaining()) {
                try {
                    chunks.add(VoxChunk.parse(buffer));
                } catch (VoxChunk.UnsupportedChunkTypeException e) {
                }
            }
        } catch (BufferUnderflowException e) {
            throw new RuntimeException("Stream ended unexpectedly while reading a data chunk", e);
        }

        return new VoxFile(version, chunks);
    }

    public Voxels get(int[] palette) {
        if (chunks.stream()
                .filter(chunk -> chunk instanceof VoxChunk.Pack)
                .findFirst()
                .isPresent()) {
            throw new UnsupportedOperationException("Multi-XYZI VOX files are not supported");
        }

        VoxChunk.Size size = chunks.stream()
                .map(chunk -> switch (chunk) {
                    case VoxChunk.Size it -> it;
                    default -> null;
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No SIZE chunk found in VOX file"));

        VoxChunk.Xyzi xyzi = chunks.stream()
                .map(chunk -> switch (chunk) {
                    case VoxChunk.Xyzi it -> it;
                    default -> null;
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No XYZI chunk found in VOX file"));

        if (xyzi.positioners().length == 0) {
            return Voxels.empty();
        }

        int[] colors = new int[size.x() * size.y() * size.z()];

        for (int positioner : xyzi.positioners()) {
            int x = (positioner & 0xFF);
            int y = (positioner >>> 8) & 0xFF;
            int z = (positioner >>> 16) & 0xFF;
            int i = (positioner >>> 24) & 0xFF;

            int index = x + (y * size.x()) + (z * size.x() * size.y());
            colors[index] = palette[i - 1];
        }

        return new Voxels(
                colors, new Vector3i(size.x(), size.y(), size.z()), new Vector3f(1f, 1f, 1f), new Vector3f(0f, 0f, 0f));
    }

    public Voxels get() {
        VoxChunk.Rgba rgba = chunks.stream()
                .map(chunk -> switch (chunk) {
                    case VoxChunk.Rgba it -> it;
                    default -> null;
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No RGBA chunk found in VOX file"));

        return get(rgba.colors());
    }
}
