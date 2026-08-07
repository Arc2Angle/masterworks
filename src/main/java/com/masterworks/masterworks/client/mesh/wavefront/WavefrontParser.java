package com.masterworks.masterworks.client.mesh.wavefront;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Supplier;

public class WavefrontParser<T, P, B extends WavefrontParser.Builder<T>> {
    private final Supplier<B> builderFactory;
    private final Map<String, Parselet<T, P, B>> parselets;

    public WavefrontParser(Supplier<B> builderFactory, Map<String, Parselet<T, P, B>> parselets) {
        this.builderFactory = builderFactory;
        this.parselets = parselets;
    }

    public interface Builder<T> {
        T build();
    }

    @FunctionalInterface
    public interface Parselet<T, P, B extends Builder<T>> {
        void parse(B builder, String content, P params);
    }

    public T parse(InputStream input, P params) throws IOException {
        B builder = builderFactory.get();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            while (true) {
                String line = reader.readLine();

                if (line == null) {
                    break;
                }

                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                int commentIndex = line.indexOf('#');
                if (commentIndex != -1) {
                    line = line.substring(0, commentIndex).trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                }

                String[] parts = line.split("\\s+", 2);

                if (parts.length < 2) {
                    throw new IOException("Line missing space: " + line);
                }

                String name = parts[0];
                String content = parts[1];

                Parselet<T, P, B> parselet = parselets.get(name);
                if (parselet == null) {
                    throw new IOException("Unknown first token: " + name);
                }

                parselet.parse(builder, content, params);
            }
        }

        return builder.build();
    }
}
