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
    private final @Nonnull Vector3ic transform;
    private final @Nullable VoxShape shape;
    private final @Nonnull Map<String, VoxModel> children;

    public VoxModel(Vector3ic transform, VoxShape shape, Map<String, VoxModel> children) {
        this.transform = transform;
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
                new Vector3i(transform),
                shape == null ? null : shape.clone(),
                children.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()
                        .clone())));
    }

    @FunctionalInterface
    public interface Submitter {
        void submit(SequencedCollection<String> path, QuadCollection quads);
    }

    public record Baked(QuadCollection quads, Map<String, Baked> children) {
        public void submit(Submitter submitter) {
            submit(submitter, new ArrayDeque<>());
        }

        private void submit(Submitter submitter, Deque<String> path) {
            if (quads != null) {
                submitter.submit(path, quads);
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
        Baked baked = new Baked(shallowBake(palette, quadsBuilderFactory), new HashMap<>());

        for (var entry : children.entrySet()) {
            String name = entry.getKey();
            VoxModel child = entry.getValue();
            baked.children.put(name, child.bake(palette, quadsBuilderFactory));
        }

        return baked;
    }

    private QuadCollection shallowBake(Palette palette, VoxQuads.Builder.Factory quadsBuilderFactory) {
        if (shape == null) {
            return null;
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

        VoxQuads.Mesh mesh = new VoxQuads.Mesh(
                colors, count, new Vector3f(count).mul(VOXEL_SIZE), new Vector3f(transform).mul(VOXEL_SIZE));
        return mesh.bake(quadsBuilderFactory.create());
    }

    public static Baked bake(List<VoxAsset> layers, VoxQuads.Builder.Factory quadsBuilderFactory) {
        if (layers.isEmpty()) {
            throw new IllegalArgumentException("At least one layer is required");
        } else if (layers.size() == 1) {
            return layers.get(0).model().bake(layers.get(0).palette(), quadsBuilderFactory);
        }

        Baked baked = new Baked(shallowBake(layers, quadsBuilderFactory), new HashMap<>());

        for (String name : layers.get(0).model().children.keySet()) {
            List<VoxAsset> childLayers = layers.stream()
                    .map(asset -> new VoxAsset(
                            Objects.requireNonNull(asset.model().children.get(name)), asset.palette()))
                    .toList();

            baked.children.put(name, bake(childLayers, quadsBuilderFactory));
        }

        return baked;
    }

    private static QuadCollection shallowBake(List<VoxAsset> layers, VoxQuads.Builder.Factory quadsBuilderFactory) {
        VoxModel top = null;

        for (VoxAsset layer : layers) {
            top = layer.model();
            if (top.shape != null) {
                break;
            }
        }

        if (top.shape == null) {
            return null;
        }

        Vector3ic count = top.shape.count();
        Vector3ic transform = top.transform;

        for (VoxAsset layer : layers) {
            VoxModel model = layer.model();

            if (!model.shape.count().equals(count)) {
                throw new IllegalArgumentException("All layers must have the same dimensions");
            }

            if (!model.transform.equals(transform)) {
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

        VoxQuads.Mesh mesh = new VoxQuads.Mesh(
                colors, count, new Vector3f(count).mul(VOXEL_SIZE), new Vector3f(transform).mul(VOXEL_SIZE));
        return mesh.bake(quadsBuilderFactory.create());
    }
}
