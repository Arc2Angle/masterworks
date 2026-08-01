package com.masterworks.masterworks.client.part.wavefront;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

public class WavefrontParser<T, B extends WavefrontParser.Builder<T>> {
    private final Supplier<B> builderFactory;
    private final Map<String, Builder.Parselet<T, B>> parselets;

    public WavefrontParser(Supplier<B> builderFactory, Map<String, Builder.Parselet<T, B>> parselets) {
        this.builderFactory = builderFactory;
        this.parselets = parselets;
    }

    public interface Builder<T> {
        T build();

        public record Context(ResourceManager resourceManager, String namespace, Path prefix) {
            public Context enter(@Nullable Path path) {
                if (path == null) {
                    // `.getParent()` returns null when the path is just a filename.
                    return this;
                }

                return new Context(resourceManager(), namespace(), prefix.resolve(path));
            }

            public InputStream open(Path path) throws IOException {
                return resourceManager()
                        .open(Identifier.fromNamespaceAndPath(
                                namespace(), prefix.resolve(path).toString()));
            }
        }

        @FunctionalInterface
        public interface Parselet<T, B extends Builder<T>> {
            void parse(B builder, Context ctx, String content);
        }
    }

    public T parse(Builder.Context ctx, InputStream input) throws IOException {
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

                Builder.Parselet<T, B> parselet = parselets.get(name);
                if (parselet == null) {
                    throw new IOException("Unknown first token: " + name);
                }

                parselet.parse(builder, ctx, content);
            }
        }

        return builder.build();
    }
}
