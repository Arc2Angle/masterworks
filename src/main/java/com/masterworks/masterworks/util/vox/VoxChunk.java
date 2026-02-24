package com.masterworks.masterworks.util.vox;

import com.masterworks.masterworks.MasterworksMod;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public sealed interface VoxChunk permits VoxChunk.Main, VoxChunk.Pack, VoxChunk.Size, VoxChunk.Xyzi, VoxChunk.Rgba {

    record Main() implements VoxChunk {
        static Main parse(ByteBuffer buffer) {
            return new Main();
        }
    }

    record Pack(int count) implements VoxChunk {
        static Pack parse(ByteBuffer buffer) {
            return new Pack(buffer.getInt());
        }
    }

    record Size(int x, int y, int z) implements VoxChunk {
        static Size parse(ByteBuffer buffer) {
            return new Size(buffer.getInt(), buffer.getInt(), buffer.getInt());
        }
    }

    record Xyzi(int[] positioners) implements VoxChunk {
        static Xyzi parse(ByteBuffer buffer) {
            int count = buffer.getInt();
            int[] positioners = new int[count];

            for (int i = 0; i < count; i++) {
                positioners[i] = buffer.getInt();
            }

            return new Xyzi(positioners);
        }
    }

    record Rgba(int[] colors) implements VoxChunk {
        // the contents of this chunk is big endian (those its headers are little endian)

        static Rgba parse(ByteBuffer buffer) {
            int[] colors = new int[256];

            buffer.order(ByteOrder.BIG_ENDIAN);

            for (int i = 0; i < 256; i++) {
                colors[i] = buffer.getInt();
            }

            buffer.order(ByteOrder.LITTLE_ENDIAN);

            return new Rgba(colors);
        }
    }

    /**
     * Parses a chunk from the given byte buffer. The buffer should be positioned at the start of the chunk (i.e. right before the 4-byte chunk type).
     * @param buffer the byte buffer to read from
     * @return the parsed chunk
     * @throws RuntimeException if the chunk type is unknown
     * @throws BufferUnderflowException if the buffer ends unexpectedly
     */
    static VoxChunk parse(ByteBuffer buffer) {
        if (buffer.order() != ByteOrder.LITTLE_ENDIAN) {
            throw new IllegalArgumentException("Buffer must be in little-endian order");
        }

        byte[] bytes = new byte[4];
        buffer.get(bytes);

        int chunkSize = buffer.getInt();
        int childChunksSize = buffer.getInt();

        return switch (new String(bytes)) {
            case "MAIN" -> Main.parse(buffer);
            case "PACK" -> Pack.parse(buffer);
            case "SIZE" -> Size.parse(buffer);
            case "XYZI" -> Xyzi.parse(buffer);
            case "RGBA" -> Rgba.parse(buffer);

            case "META" -> throw UnsupportedChunkTypeException.consume("META", buffer, chunkSize, childChunksSize);
            case "NOTE" -> throw UnsupportedChunkTypeException.consume("NOTE", buffer, chunkSize, childChunksSize);
            case "LAYR" -> throw UnsupportedChunkTypeException.consume("LAYR", buffer, chunkSize, childChunksSize);
            case "MATL" -> throw UnsupportedChunkTypeException.consume("MATL", buffer, chunkSize, childChunksSize);

            case "nTRN" -> throw UnsupportedChunkTypeException.consume("nTRN", buffer, chunkSize, childChunksSize);
            case "nGRP" -> throw UnsupportedChunkTypeException.consume("nGRP", buffer, chunkSize, childChunksSize);
            case "nSHP" -> throw UnsupportedChunkTypeException.consume("nSHP", buffer, chunkSize, childChunksSize);

            case "rCAM" -> throw UnsupportedChunkTypeException.consume("rCAM", buffer, chunkSize, childChunksSize);
            case "rOBJ" -> throw UnsupportedChunkTypeException.consume("rOBJ", buffer, chunkSize, childChunksSize);

            case String type ->
                throw UnsupportedChunkTypeException.warnConsume(type, buffer, chunkSize, childChunksSize);
        };
    }

    class UnsupportedChunkTypeException extends RuntimeException {
        public UnsupportedChunkTypeException(String type) {
            super("Unsupported chunk type \"" + type + "\"");
        }

        public static UnsupportedChunkTypeException consume(
                String type, ByteBuffer buffer, int chunkSize, int childChunksSize) {
            buffer.position(buffer.position() + chunkSize + childChunksSize);
            return new UnsupportedChunkTypeException(type);
        }

        public static UnsupportedChunkTypeException warnConsume(
                String type, ByteBuffer buffer, int chunkSize, int childChunksSize) {
            byte[] bytes = new byte[chunkSize + childChunksSize];
            buffer.get(bytes);

            if (bytes.length < 1024) {
                MasterworksMod.LOGGER.warn(
                        "Skipping unsupported chunk type \"{}\" with contents: \"{}\"", type, new String(bytes));
            } else {
                MasterworksMod.LOGGER.warn(
                        "Skipping unsupported chunk type \"{}\" with contents: <{} bytes>", type, bytes.length);
            }

            return new UnsupportedChunkTypeException(type);
        }
    }
}
