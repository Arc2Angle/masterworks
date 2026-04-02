package com.masterworks.masterworks.client.vox;

import com.masterworks.masterworks.client.palette.Palette;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SequencedCollection;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.resources.model.QuadCollection;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import org.joml.Vector3ic;

public class VoxModel implements Cloneable {
    private final @Nonnull Vector3ic translation;
    private final @Nullable VoxShape shape;
    private final @Nonnull Map<String, VoxModel> children;

    public VoxModel(Vector3ic translation, VoxShape shape, Map<String, VoxModel> children) {
        this.translation = translation;
        this.shape = shape;
        this.children = children;
    }

    public Optional<VoxShape> single() {
        return switch (children.size()) {
            case 0 -> Optional.ofNullable(shape);
            case 1 -> shape == null ? children.values().iterator().next().single() : Optional.empty();
            default -> Optional.empty();
        };
    }

    @Override
    public VoxModel clone() {
        return new VoxModel(
                new Vector3i(translation),
                shape == null ? null : shape.clone(),
                children.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()
                        .clone())));
    }

    @FunctionalInterface
    public interface Submitter {
        void submit(
                SequencedCollection<String> path,
                @Nonnull QuadCollection quadCollection,
                @Nonnull Vector3fc translation);
    }

    public record Baked(Vector3fc translation, @Nullable QuadCollection quads, Map<String, Baked> children) {
        public void submit(Submitter submitter) {
            submit(submitter, new ArrayDeque<>());
        }

        private void submit(Submitter submitter, Deque<String> path) {
            if (quads != null) {
                submitter.submit(path, quads, translation);
            }

            for (var entry : children.entrySet()) {
                String name = entry.getKey();
                Baked child = entry.getValue();

                path.push(name);
                child.submit(submitter, path);
                path.pop();
            }
        }
    }

    private static final Vector3fc VOXEL_SIZE = new Vector3f(1 / 16f, 1 / 16f, 1 / 16f);

    public Baked bake(Palette palette, VoxQuads.Builder.Factory quadsBuilderFactory) {
        Baked baked = shallowBake(palette, quadsBuilderFactory);

        for (var entry : children.entrySet()) {
            String name = entry.getKey();
            VoxModel child = entry.getValue();
            baked.children.put(name, child.bake(palette, quadsBuilderFactory));
        }

        return baked;
    }

    private Baked shallowBake(Palette palette, VoxQuads.Builder.Factory quadsBuilderFactory) {
        if (shape == null) {
            return new Baked(new Vector3f(translation).mul(VOXEL_SIZE), null, new HashMap<>());
        }

        Vector3ic count = shape.count();

        int[] colors = new int[count.x() * count.y() * count.z()];

        for (int i = 0; i < colors.length; i++) {
            byte colorIndex = shape.colorIndices()[i];

            if (colorIndex == 0) {
                continue;
            }

            colors[i] = palette.getColorByIndex(colorIndex - 1);
        }

        VoxQuads.Mesh mesh = new VoxQuads.Mesh(colors, count, VOXEL_SIZE);
        return new Baked(
                new Vector3f(translation).mul(VOXEL_SIZE), mesh.bake(quadsBuilderFactory.create()), new HashMap<>());
    }

    public static Baked bake(List<VoxAsset> layers, VoxQuads.Builder.Factory quadsBuilderFactory) {
        if (layers.isEmpty()) {
            throw new IllegalArgumentException("At least one layer is required");
        } else if (layers.size() == 1) {
            return layers.get(0).model().bake(layers.get(0).palette(), quadsBuilderFactory);
        }

        Baked baked = shallowBake(layers, quadsBuilderFactory);

        for (String name : layers.get(0).model().children.keySet()) {
            List<VoxAsset> childLayers = layers.stream()
                    .map(asset -> new VoxAsset(
                            Objects.requireNonNull(asset.model().children.get(name)), asset.palette()))
                    .toList();

            baked.children.put(name, bake(childLayers, quadsBuilderFactory));
        }

        return baked;
    }

    private static Baked shallowBake(List<VoxAsset> layers, VoxQuads.Builder.Factory quadsBuilderFactory) {
        VoxModel top = layers.stream()
                .map(VoxAsset::model)
                .filter(model -> model.shape != null)
                .findFirst()
                .orElse(null);

        if (top == null) {
            return new Baked(new Vector3f(layers.get(0).model().translation).mul(VOXEL_SIZE), null, new HashMap<>());
        }

        Vector3ic count = top.shape.count();
        Vector3ic translation = top.translation;

        for (VoxAsset layer : layers) {
            VoxModel model = layer.model();

            if (!model.shape.count().equals(count)) {
                throw new IllegalArgumentException("All layers must have the same dimensions");
            }

            if (!model.translation.equals(translation)) {
                throw new IllegalArgumentException("All layers must have the same transform");
            }
        }

        int[] colors = new int[count.x() * count.y() * count.z()];

        for (int i = 0; i < colors.length; i++) {
            for (VoxAsset layer : layers) {
                byte colorIndex = layer.model().shape.colorIndices()[i];

                if (colorIndex == 0) {
                    continue;
                }

                colors[i] = layer.palette().getColorByIndex(colorIndex - 1);
                break;
            }
        }

        VoxQuads.Mesh mesh = new VoxQuads.Mesh(colors, count, VOXEL_SIZE);
        return new Baked(
                new Vector3f(translation).mul(VOXEL_SIZE), mesh.bake(quadsBuilderFactory.create()), new HashMap<>());
    }
}
