package com.masterworks.masterworks.util.vox;

import net.minecraft.util.ARGB;
import org.joml.Vector3f;
import org.joml.Vector3i;

/**
 * A native 3D dense voxels object.
 *
 * @param colors A dense array of ARGB color values for each voxel in the model.
 *     Invisible voxels (alpha = 0) are considered empty, even if they have
 *     non-zero RGB values.
 * @param count  Represents the number of voxels along each axis (x, y, z).
 * @param size   Represents the overall size of the voxel model in each
 *     dimension (x, y, z).
 * @param offset Represents the offset position of the voxel model (x, y, z).
 *
 * @apiNote Sizes and offsets are in minecraft blocks.
 */
public record Voxels(int[] colors, Vector3i count, Vector3f size, Vector3f offset) {

    public static Voxels empty() {
        return new Voxels(new int[0], new Vector3i(0, 0, 0), new Vector3f(0f, 0f, 0f), new Vector3f(0f, 0f, 0f));
    }

    /**
     * Compacts a voxel model by trimming empty space around it.
     * @return A new {@code Voxels} instance with tight bounds.
     */
    public Voxels compact() {
        Vector3i lower = new Vector3i(count.x, count.y, count.z);
        Vector3i upper = new Vector3i(-1, -1, -1);

        for (int x = 0; x < count.x; x++) {
            for (int y = 0; y < count.y; y++) {
                for (int z = 0; z < count.z; z++) {
                    if (!hasColorAt(x, y, z)) {
                        continue;
                    }

                    Vector3i pos = new Vector3i(x, y, z);
                    lower.min(pos);
                    upper.max(pos);
                }
            }
        }

        if (upper.x == -1 || upper.y == -1 || upper.z == -1) {
            return empty();
        }

        return trim(lower, upper);
    }

    /**
     * Trims a voxel model to the given bounds, removing any voxels outside of
     * them. The size and offset are adjusted such that the remaining voxels have
     * the same positioning as they had before.
     * @param lower The lower bounds to trim to.
     * @param upper The upper bounds to trim to.
     * @return A new {@code Voxels} instance with the trimmed bounds.
     */
    public Voxels trim(Vector3i lower, Vector3i upper) {
        Vector3i rCount = new Vector3i(upper.x - lower.x + 1, upper.y - lower.y + 1, upper.z - lower.z + 1);
        int[] rColors = new int[rCount.x * rCount.y * rCount.z];

        for (int x = 0; x < count.x; x++) {
            for (int y = 0; y < count.y; y++) {
                for (int z = 0; z < count.z; z++) {
                    int color = getColorAt(x, y, z);

                    if (ARGB.alpha(color) == 0) {
                        continue;
                    }

                    Vector3i pos = new Vector3i(x, y, z);
                    Vector3i rPos = pos.sub(lower);

                    int rIndex = rPos.x + rPos.y * rCount.x + rPos.z * rCount.x * rCount.y;
                    rColors[rIndex] = color;
                }
            }
        }

        Vector3f rSize = new Vector3f(
                        (float) rCount.x / (float) count.x,
                        (float) rCount.y / (float) count.y,
                        (float) rCount.z / (float) count.z)
                .mul(size);

        Vector3f rOffset = new Vector3f(
                        (float) lower.x / (float) count.x * size.x,
                        (float) lower.y / (float) count.y * size.y,
                        (float) lower.z / (float) count.z * size.z)
                .add(offset);

        return new Voxels(rColors, rCount, rSize, rOffset);
    }

    /**
     * @return A deep copy of this voxel model.
     */
    public Voxels copy() {
        return new Voxels(colors.clone(), new Vector3i(count), new Vector3f(size), new Vector3f(offset));
    }

    /**
     * Overlays another {@code Voxels} on top of this one, such the fully
     * transparent voxels in the other one reveal the corresponding voxels from
     * this one.
     * @param other The {@code Voxels} object to overlay on top of this one.
     * @return This {@code Voxels} object.
     * @throws IllegalArgumentException if the two {@code Voxels} objects have
     *     different counts, sizes, or offsets.
     */
    public Voxels overlay(Voxels other) {
        if (!count.equals(other.count)) {
            throw new IllegalArgumentException(
                    "Cannot overlay voxels with different counts: " + count + " vs " + other.count);
        }

        if (!size.equals(other.size)) {
            throw new IllegalArgumentException(
                    "Cannot overlay voxels with different sizes: " + size + " vs " + other.size);
        }

        if (!offset.equals(other.offset)) {
            throw new IllegalArgumentException(
                    "Cannot overlay voxels with different offsets: " + offset + " vs " + other.offset);
        }

        for (int i = 0; i < colors.length; i++) {
            int overlayed = other.colors[i];
            if (ARGB.alpha(overlayed) != 0) {
                colors[i] = overlayed;
            }
        }

        return this;
    }

    /**
     * Checks the color at the given coordinates.
     * @return True if the alpha value of the color is not zero, false otherwise.
     */
    public boolean hasColorAt(int x, int y, int z) {
        return ARGB.alpha(getColorAt(x, y, z)) != 0;
    }

    /**
     * Gets the color at the given coordinates.
     * @return The color's ARGB value.
     */
    public int getColorAt(int x, int y, int z) {
        return colors[x + (y * count.x) + (z * count.x * count.y)];
    }

    /**
     * Sets the color at the given coordinates.
     * @param color The color's ARGB value. Fully transparent colors (alpha = 0) are considered empty, even if they have non-zero RGB values.
     */
    public void setColorAt(int x, int y, int z, int color) {
        colors[x + (y * count.x) + (z * count.x * count.y)] = color;
    }

    /**
     * Scales the RGB values of the color at the given coordinates by the given factor, leaving the alpha value unchanged.
     * @param scale The scaling factor, between 0 and 255, where 0 means the RGB values are completely removed, and 255 means the RGB values are unchanged.
     */
    public void scaleColorAt(int x, int y, int z, int scale) {
        int index = x + (y * count.x) + (z * count.x * count.y);
        colors[index] = ARGB.scaleRGB(colors[index], scale);
    }
}
