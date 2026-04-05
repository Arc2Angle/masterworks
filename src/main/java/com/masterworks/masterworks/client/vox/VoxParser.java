package com.masterworks.masterworks.client.vox;

import com.masterworks.masterworks.util.math.Vector3b;
import com.masterworks.masterworks.util.math.Vector3bc;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.joml.Vector3i;
import org.joml.Vector3ic;

public interface VoxParser<T extends VoxChunk> {

    String header();

    /**
     * Parses a chunk from the given buffer.
     * @param buffer The buffer to parse the chunk from, positioned after the chunk header bytes. Must be little endian.
     * @return The parsed chunk.
     */
    T parse(ByteBuffer buffer) throws ParseException;

    /**
     * Parses all chunks from the given buffer, using the given parsers for the main chunk types and the default parser for any unsupported chunk types.
     * @param buffer The buffer to parse the chunks from. Must be little endian.
     * @param mainParsers The parsers for the main chunk types. The header of each parser must be unique.
     * @param defaultParser Creates a parser for any unsupported chunk based on its header.
     * @return The list of parsed chunks, in the order they were parsed.
     */
    public static List<VoxChunk> parseAll(
            ByteBuffer buffer, List<VoxParser<?>> mainParsers, Function<String, VoxParser<?>> defaultParser)
            throws ParseException {
        Map<String, VoxParser<?>> parsers =
                mainParsers.stream().collect(Collectors.toMap(VoxParser::header, Function.identity()));
        List<VoxChunk> chunks = new ArrayList<>();

        while (buffer.hasRemaining()) {
            String header = ParseException.ofBufferUnderflow(() -> parseString(4, buffer), "chunk header");

            VoxParser<?> parser = parsers.get(header);
            if (parser == null) {
                parser = defaultParser.apply(header);
            }

            VoxChunk chunk = parser.parse(buffer);
            chunks.add(chunk);
        }

        return chunks;
    }

    public abstract class Sized<T extends VoxChunk> implements VoxParser<T> {
        @Override
        public final T parse(ByteBuffer buffer) throws ParseException {
            int chunkSize = ParseException.ofBufferUnderflow(buffer::getInt, "chunk size");
            int childChunksSize = ParseException.ofBufferUnderflow(buffer::getInt, "chunk children size");

            try {
                return parse(buffer, chunkSize, childChunksSize);
            } catch (BufferUnderflowException e) {
                throw ParseException.ofBufferUnderflow("chunk", e);
            }
        }

        /**
         * Parses a chunk from the given buffer.
         * @param buffer The buffer to parse the chunk from, positioned after the chunk header bytes, the chunk size bytes, and the child chunks size bytes. Must be little endian.
         * @param chunkSize The size of the chunk.
         * @param childChunksSize The size of the child chunks.
         * @return The parsed chunk.
         */
        protected abstract T parse(ByteBuffer buffer, int chunkSize, int childChunksSize) throws ParseException;
    }

    public class Header implements VoxParser<VoxChunk.Header> {
        @Override
        public String header() {
            return "VOX ";
        }

        @Override
        public VoxChunk.Header parse(ByteBuffer buffer) {
            return new VoxChunk.Header(buffer.getInt());
        }
    }

    public class Main extends Sized<VoxChunk.Main> {
        @Override
        public String header() {
            return "MAIN";
        }

        @Override
        protected VoxChunk.Main parse(ByteBuffer buffer, int chunkSize, int childChunksSize) {
            return new VoxChunk.Main();
        }
    }

    public class Pack extends Sized<VoxChunk.Pack> {
        @Override
        public String header() {
            return "PACK";
        }

        @Override
        protected VoxChunk.Pack parse(ByteBuffer buffer, int chunkSize, int childChunksSize) {
            return new VoxChunk.Pack(buffer.getInt());
        }
    }

    public class Size extends Sized<VoxChunk.Size> {
        @Override
        public String header() {
            return "SIZE";
        }

        @Override
        protected VoxChunk.Size parse(ByteBuffer buffer, int chunkSize, int childChunksSize) {
            return new VoxChunk.Size(buffer.getInt(), buffer.getInt(), buffer.getInt());
        }
    }

    public class Xyzi extends Sized<VoxChunk.Xyzi> {
        @Override
        public String header() {
            return "XYZI";
        }

        @Override
        protected VoxChunk.Xyzi parse(ByteBuffer buffer, int chunkSize, int childChunksSize) {
            int count = buffer.getInt();
            int[] positioners = new int[count];

            for (int i = 0; i < count; i++) {
                positioners[i] = buffer.getInt();
            }

            return new VoxChunk.Xyzi(positioners);
        }
    }

    public class Rgba extends Sized<VoxChunk.Rgba> {
        @Override
        public String header() {
            return "RGBA";
        }

        @Override
        protected VoxChunk.Rgba parse(ByteBuffer buffer, int chunkSize, int childChunksSize) {
            int[] colors = new int[256];

            // for some reason, the contents of this chunk are big endian (those its headers are still little endian)
            buffer.order(ByteOrder.BIG_ENDIAN);

            for (int i = 0; i < 256; i++) {
                colors[i] = buffer.getInt();
            }

            buffer.order(ByteOrder.LITTLE_ENDIAN);

            return new VoxChunk.Rgba(colors);
        }
    }

    public class Ntrn extends Sized<VoxChunk.Ntrn> {
        @Override
        public String header() {
            return "nTRN";
        }

        @Override
        protected VoxChunk.Ntrn parse(ByteBuffer buffer, int chunkSize, int childChunksSize) throws ParseException {
            int nodeId = buffer.getInt();
            String name = parseDictionary(buffer).getString("_name").orElse("");
            int childNodeId = buffer.getInt();

            ParseException.expect(buffer.getInt(), -1, "nTRN reserved id");
            int layerId = buffer.getInt();

            ParseException.expect(buffer.getInt(), 1, "nTRN keyframe count");
            Private.Dictionary properties = parseDictionary(buffer);
            Vector3ic translation = properties.getTranslation("_t").orElse(new Vector3i());
            Vector3bc rotation = properties.getRotation("_r").orElse(new Vector3b());

            return new VoxChunk.Ntrn(nodeId, name, childNodeId, layerId, translation, rotation);
        }
    }

    public class Ngrp extends Sized<VoxChunk.Ngrp> {
        @Override
        public String header() {
            return "nGRP";
        }

        @Override
        protected VoxChunk.Ngrp parse(ByteBuffer buffer, int chunkSize, int childChunksSize) {
            int nodeId = buffer.getInt();
            parseDictionary(buffer);

            int childCount = buffer.getInt();
            int[] childNodeIds = new int[childCount];

            for (int i = 0; i < childCount; i++) {
                childNodeIds[i] = buffer.getInt();
            }

            return new VoxChunk.Ngrp(nodeId, childNodeIds);
        }
    }

    public class Nshp extends Sized<VoxChunk.Nshp> {
        @Override
        public String header() {
            return "nSHP";
        }

        @Override
        protected VoxChunk.Nshp parse(ByteBuffer buffer, int chunkSize, int childChunksSize) throws ParseException {
            int nodeId = buffer.getInt();
            parseDictionary(buffer);

            ParseException.expect(buffer.getInt(), 1, "nSHP shape count");
            int shapeId = buffer.getInt();
            parseDictionary(buffer);

            return new VoxChunk.Nshp(nodeId, shapeId);
        }
    }

    public class Unsupported extends Sized<VoxChunk.Unsupported> {
        private final String header;

        public Unsupported(String header) {
            this.header = header;
        }

        @Override
        public String header() {
            return header;
        }

        @Override
        protected VoxChunk.Unsupported parse(ByteBuffer buffer, int chunkSize, int childChunksSize)
                throws ParseException {
            byte[] contents = new byte[chunkSize + childChunksSize];
            try {
                buffer.get(contents);
            } catch (BufferUnderflowException e) {
                throw ParseException.ofBufferUnderflow("unsupported chunk \"" + header + "\" contents", e);
            }
            return new VoxChunk.Unsupported(header, chunkSize, childChunksSize, contents);
        }
    }

    public class ParseException extends Exception {
        public ParseException(String message) {
            super(message);
        }

        public ParseException(String message, Throwable cause) {
            super(message, cause);
        }

        public static <T> void expect(T value, T expected, String field) throws ParseException {
            if (!value.equals(expected)) {
                throw new ParseException("Expected " + field + " to be " + expected + ", but got " + value);
            }
        }

        public static ParseException ofBufferUnderflow(String field, Throwable cause) {
            return new ParseException("Buffer ended unexpectedly while reading " + field, cause);
        }

        public static <T> T ofBufferUnderflow(Supplier<T> supplier, String field) throws ParseException {
            try {
                return supplier.get();
            } catch (BufferUnderflowException e) {
                throw ofBufferUnderflow(field, e);
            }
        }
    }

    private static String parseString(int length, ByteBuffer buffer) {
        byte[] bytes = new byte[length];
        buffer.get(bytes);
        return new String(bytes);
    }

    private static String parseString(ByteBuffer buffer) {
        int length = buffer.getInt();
        byte[] bytes = new byte[length];
        buffer.get(bytes);
        return new String(bytes);
    }

    private static Private.Dictionary parseDictionary(ByteBuffer buffer) {
        int count = buffer.getInt();

        Map<String, String> dictionary = new HashMap<>(count);

        for (int i = 0; i < count; i++) {
            String key = parseString(buffer);
            String value = parseString(buffer);
            dictionary.put(key, value);
        }

        return new Private.Dictionary(dictionary);
    }

    class Private {
        private static final class Dictionary {
            private final Map<String, String> values;

            public Dictionary(Map<String, String> values) {
                this.values = values;
            }

            public Optional<String> getString(String key) {
                return Optional.ofNullable(values.get(key));
            }

            public Optional<Vector3ic> getTranslation(String key) throws ParseException {
                String value = values.get(key);
                if (value == null) {
                    return Optional.empty();
                }

                String[] parts = value.split(" ");
                if (parts.length != 3) {
                    throw new ParseException("Expected vector string to have 3 parts, but got " + parts.length);
                }

                try {
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    int z = Integer.parseInt(parts[2]);
                    return Optional.of(new Vector3i(x, y, z));
                } catch (NumberFormatException e) {
                    throw new ParseException("Expected vector parts to be integers, but got: " + value, e);
                }
            }

            public Optional<Vector3bc> getRotation(String key) throws ParseException {
                String value = values.get(key);
                if (value == null) {
                    return Optional.empty();
                }

                int packed;
                try {
                    packed = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    throw new ParseException("Expected packed rotation value to be an integer, but got: " + value, e);
                }

                int axis0 = (packed >> 0) & 0b11;
                int axis1 = (packed >> 2) & 0b11;
                int axis2 = 3 - (axis0 + axis1);

                int flip0 = (packed >> 4) & 0b1;
                int flip1 = (packed >> 5) & 0b1;
                int flip2 = (packed >> 6) & 0b1;

                boolean x = (axis0 == 0 && flip0 == 1) || (axis1 == 0 && flip1 == 1) || (axis2 == 0 && flip2 == 1);
                boolean y = (axis0 == 1 && flip0 == 1) || (axis1 == 1 && flip1 == 1) || (axis2 == 1 && flip2 == 1);
                boolean z = (axis0 == 2 && flip0 == 1) || (axis1 == 2 && flip1 == 1) || (axis2 == 2 && flip2 == 1);

                return Optional.of(new Vector3b(x, y, z));
            }
        }
    }
}
