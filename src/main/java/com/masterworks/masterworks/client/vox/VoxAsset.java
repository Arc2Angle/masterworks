package com.masterworks.masterworks.client.vox;

import com.masterworks.masterworks.client.palette.Palette;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.joml.Vector3ic;

public record VoxAsset(VoxModel model, Palette palette) {

    public static VoxAsset compile(List<VoxChunk> chunks) {
        List<VoxChunk.Size> sizeChunks = new ArrayList<>();
        List<VoxChunk.Xyzi> xyziChunks = new ArrayList<>();
        Map<Integer, VoxChunk.Node> nodeChunks = new HashMap<>();
        Optional<VoxChunk.Rgba> rgbaChunk = Optional.empty();

        for (VoxChunk chunk : chunks) {
            switch (chunk) {
                case VoxChunk.Size size -> {
                    sizeChunks.add(size);
                }

                case VoxChunk.Xyzi xyzi -> {
                    xyziChunks.add(xyzi);
                }

                case VoxChunk.Node node -> {
                    nodeChunks.put(node.id(), node);
                }

                case VoxChunk.Rgba rgba -> {
                    if (rgbaChunk.isPresent()) {
                        throw new IllegalStateException(
                                "Expected at most one RGBA chunk to define the palette, but found multiple");
                    }

                    rgbaChunk = Optional.of(rgba);
                }

                default -> {}
            }
        }

        if (sizeChunks.size() != xyziChunks.size()) {
            throw new IllegalStateException("Expected equal number of SIZE and XYZI chunks, but found "
                    + sizeChunks.size() + " and " + xyziChunks.size());
        }

        // Shape calculation

        List<VoxShape> shapes = new ArrayList<>(sizeChunks.size());

        for (int i = 0; i < sizeChunks.size(); i++) {
            VoxChunk.Size size = sizeChunks.get(i);
            VoxChunk.Xyzi xyzi = xyziChunks.get(i);

            byte[] colors = new byte[size.x() * size.y() * size.z()];
            for (int positioner : xyzi.positioners()) {
                int x = (positioner & 0xFF);
                int y = (positioner >>> 8) & 0xFF;
                int z = (positioner >>> 16) & 0xFF;
                int color = (positioner >>> 24) & 0xFF;
                // color indices are 1-based in the VOX format, 0 is reserved for empty voxels

                int index = x + (y * size.x()) + (z * size.x() * size.y());
                colors[index] = (byte) color;
            }

            shapes.add(new VoxShape(size.x(), size.y(), size.z(), colors));
        }

        // Graph calculation

        final class Node {
            private static final int NO_SHAPE = -1;

            public String name;
            public Vector3ic transform;
            public int shapeId = NO_SHAPE;
            public List<Node> children = new ArrayList<>();

            public VoxModel build(List<VoxShape> shapes) {
                VoxShape shape = shapeId != NO_SHAPE ? shapes.get(shapeId) : null;

                Map<String, VoxModel> children = new HashMap<>(this.children.size());
                for (Node child : this.children) {
                    children.put(child.name, child.build(shapes));
                }

                return new VoxModel(transform, shape, children);
            }
        }

        Node root = new Node();
        Deque<Map.Entry<Integer, Node>> stack = new ArrayDeque<>();
        stack.push(Map.entry(0, root));

        while (!stack.isEmpty()) {
            Map.Entry<Integer, Node> entry = stack.pop();
            int id = entry.getKey();
            Node node = entry.getValue();

            switch (nodeChunks.remove(id)) {
                case VoxChunk.Ntrn(int _, String name, int childNodeId, int _, Vector3ic transform) -> {
                    node.name = name;
                    node.transform = transform;

                    stack.push(Map.entry(childNodeId, node)); // merge with child
                }

                case VoxChunk.Ngrp(int _, int[] childNodeIds) -> {
                    for (int childNodeId : childNodeIds) {
                        Node child = new Node();
                        node.children.add(child);
                        stack.push(Map.entry(childNodeId, child));
                    }
                }

                case VoxChunk.Nshp(int _, int shapeId) -> {
                    node.shapeId = shapeId;
                }

                case null -> {
                    throw new IllegalStateException("Node with id " + id + " is referenced but not defined");
                }
            }
        }

        if (!nodeChunks.isEmpty()) {
            throw new IllegalStateException("Expected all graph nodes to be connected to the root, but found "
                    + nodeChunks.size() + " unconnected nodes");
        }

        Palette palette = rgbaChunk
                .map(rgba -> Palette.fromRGBA(rgba.colors()))
                .orElseThrow(
                        () -> new IllegalStateException("Expected a RGBA chunk to define the palette, but found none"));

        return new VoxAsset(root.build(shapes), palette);
    }
}
