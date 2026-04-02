package com.masterworks.masterworks.client.vox;

import com.masterworks.masterworks.MasterworksMod;
import org.joml.Vector3ic;

public sealed interface VoxChunk
        permits VoxChunk.Header,
                VoxChunk.Main,
                VoxChunk.Pack,
                VoxChunk.Size,
                VoxChunk.Xyzi,
                VoxChunk.Rgba,
                VoxChunk.Node,
                VoxChunk.Unsupported {

    // "NOTE", "LAYR", "MATL", "rCAM", "rOBJ"

    public record Header(int version) implements VoxChunk {}

    public record Main() implements VoxChunk {}

    public record Pack(int count) implements VoxChunk {}

    public record Size(int x, int y, int z) implements VoxChunk {}

    public record Xyzi(int[] positioners) implements VoxChunk {}

    public record Rgba(int[] colors) implements VoxChunk {}

    public sealed interface Node extends VoxChunk permits VoxChunk.Ntrn, VoxChunk.Ngrp, VoxChunk.Nshp {
        int id();
    }

    public record Ntrn(int id, String name, int childNodeId, int layerId, Vector3ic translation) implements Node {}

    public record Ngrp(int id, int[] childNodeIds) implements Node {}

    public record Nshp(int id, int shapeId) implements Node {}

    public record Unsupported(String header, int chunkSize, int childChunksSize, byte[] contents) implements VoxChunk {
        public Unsupported warn() {
            if (contents.length < 1024) {
                MasterworksMod.LOGGER.warn(
                        "Skipping unsupported chunk \"{}\" with contents: \"{}\"", header, new String(contents));
            } else {
                MasterworksMod.LOGGER.warn(
                        "Skipping unsupported chunk \"{}\" with contents: <{} bytes>", header, contents.length);
            }
            return this;
        }
    }
}
